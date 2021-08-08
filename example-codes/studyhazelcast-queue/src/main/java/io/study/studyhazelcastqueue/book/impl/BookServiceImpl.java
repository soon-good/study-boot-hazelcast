package io.study.studyhazelcastqueue.book.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

import io.study.studyhazelcastqueue.book.Book;
import io.study.studyhazelcastqueue.book.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
