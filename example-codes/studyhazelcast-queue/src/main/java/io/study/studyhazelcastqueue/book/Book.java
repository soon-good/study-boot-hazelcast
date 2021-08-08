package io.study.studyhazelcastqueue.book;

import java.io.Serializable;

import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Book implements Serializable {

	private Long id;
	private String bookName;
	private Long authorId;
	private Long publishingCompanyId;

	@Builder
	public Book(String bookName, Long authorId, Long publishingCompanyId){
		this.bookName = bookName;
		this.authorId = authorId;
		this.publishingCompanyId = publishingCompanyId;
	}
}
