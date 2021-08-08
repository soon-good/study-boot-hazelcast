package io.study.studyhazelcastqueue.queue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hazelcast.core.HazelcastInstance;

import io.study.studyhazelcastqueue.book.Book;
import io.study.studyhazelcastqueue.book.BookService;
import lombok.extern.slf4j.Slf4j;

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
