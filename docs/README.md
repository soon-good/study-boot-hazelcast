# HazelCast 기반 인메모리 캐시 큐 구현해보기

Hazelcast 에 대한 개념적인 설명과 성능 벤치마크, 다중화 가능여부는 이번 주 주말 쯤에 추가하게 되지 않을까 싶다. 

> 참고) <br>
>
> - EhCache 3 도 캐시를 구현하고 있고 캐시 기반 큐를 제공했었지만(TerraCota) 관련 링크는 현재 사라진 상태이다. 

<br>

## 의존성 추가

이번에도 Maven 이 기본으로 선택되어있는줄 모르고 Next 만 누르다가 메이븐 프로젝트를 생성해버려서 그냥 메이븐 기반 의존성으로 추가해주었다.

```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
  </dependency>

  <!-- ... -->

  <dependency>
    <groupId>com.hazelcast</groupId>
    <artifactId>hazelcast</artifactId>
    <version>4.2</version>
  </dependency>
  
  <dependency>
    <groupId>com.hazelcast</groupId>
    <artifactId>hazelcast-spring</artifactId>
    <version>4.2</version>
  </dependency>

</dependencies>
```



## 설정 코드

```java
@Configuration
public class HazelcastConfig {

	@Bean(name = "hazelcastInstance")
	public HazelcastInstance hazelcastInstance(
		@Qualifier("hazelcastBufferConfig") Config config
	){
		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean(name = "hazelcastBufferConfig")
	public Config hazelcastBufferConfig(
		@Qualifier("hazelcastQueueConfig") QueueConfig queueConfig
	){
		Config config = new Config();
		config.setInstanceName("hazelcast-test")
			.addQueueConfig(queueConfig);

		return config;
	}

	@Bean(name = "hazelcastQueueConfig")
	public QueueConfig hazelcastQueueConfig(){
		return new QueueConfig()
			.setName("hazelcastQueueConfig")
			.setMaxSize(1000);
	}
}
```

<br>

각 @Bean 인스턴스의 의존관계는 `HazelcastInstance <- Config <- QueueConfig` 의 순서로 이루어진다.<br>

<br>

## Queue 서비스 로직

Book 이라는 도메인에 대해 Queue 에 넣어두고 꺼내는 로직을 서비스로직으로 풀어보면 아래와 같다.

**BookService.java**

```java
public interface BookService {

	Book offerBook(Book book);

	void offerBookList(List<Book> bookList);

	Book pollBook();

	Object pollObject();
}
```

<br>

**BookServiceImpl.java**

```java
@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

   private final HazelcastInstance hazelcastInstance;

   @Override
   public Book offerBook(Book book) {
      IQueue<Book> queue = hazelcastInstance.getQueue("book-queue");
      queue.offer(book);
      return book;
   }

   @Override
   public void offerBookList(List<Book> bookList) {
      IQueue<Book> queue = hazelcastInstance.getQueue("book-queue");
      queue.addAll(bookList);
   }

   @Override
   public Book pollBook() {
      IQueue<Book> queue = hazelcastInstance.getQueue("book-queue");
      return queue.poll();
   }

   @Override
   public Object pollObject() {
      IQueue<Object> queue = hazelcastInstance.getQueue("book-queue");
      return queue.poll();
   }
}
```

각 메서드들의 역할을 정리해보면 이렇다.

- offerBook(book : Book) : Book
  - Queue 에 Book 을 넣는다.
- offerBookList (`List<Book> bookList` )
  - list 타입의 bookList 를 Queue에 한꺼번에 넣는다.
- pollBook() : Book
  - Queue에서 가장 먼저 들어온 요소를 poll() 한다.
- pollObject : Object
  - 별 의미 없이 IQueue<Object> 로 받는 경우도 있다는 것을 확인해보기 위해 그냥 작성해본 코드다.

<br>

## 테스트 코드

```java
package io.study.studyhazelcastqueue.queue;

// ...

@Slf4j
@SpringBootTest
public class SampleQueueTest {

	private List<Book> books = null;

	@Autowired
	private BookService bookService;

	@BeforeEach
	void init(){
		Book aBookC = Book.builder()
			.bookName("A Book on C")
			.publishingCompanyId(1L)
			.authorId(1L)
			.build();

		Book aBookJava = Book.builder()
			.bookName("A Book on Java")
			.publishingCompanyId(2L)
			.authorId(2L)
			.build();

		books = Arrays.asList(aBookC, aBookJava);
	}

	@Test
	@DisplayName("테스트1_단건으로_offer_후_단건으로_poll")
	public void 테스트1_단건으로_offer_후_단건으로_poll(){
		for(Book book : books){
			log.info("offer book :: " + book);
			bookService.offerBook(book);
		}

		Book book1 = bookService.pollBook();
		Book book2 = bookService.pollBook();

		log.info("book1 = {}", book1);
		log.info("book2 = {}", book2);
	}

	@Test
	@DisplayName("테스트2_리스트로_offer_후_단건으로_poll")
	public void 테스트2_리스트로_offer_후_단건으로_poll(){
		bookService.offerBookList(books);

		Book book1 = (Book)bookService.pollObject();
		Book book2 = (Book)bookService.pollObject();

		log.info("book1 = {}", book1);
		log.info("book2 = {}", book2);
	}

	@Test
	@DisplayName("테스트3_리스트로_offer_후_pollBook")
	public void 테스트3_리스트로_offer_후_pollBook(){
		bookService.offerBookList(books);

		Book book1 = bookService.pollBook();
		Book book2 = bookService.pollBook();

		log.info("book1 = {}", book1);
		log.info("book2 = {}", book2);
	}
}
```



