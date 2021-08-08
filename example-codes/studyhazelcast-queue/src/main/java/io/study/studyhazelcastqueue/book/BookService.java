package io.study.studyhazelcastqueue.book;

import java.util.List;

public interface BookService {

	Book offerBook(Book book);

	void offerBookList(List<Book> bookList);

	Book pollBook();

	Object pollObject();
}
