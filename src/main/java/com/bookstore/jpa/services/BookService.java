package com.bookstore.jpa.services;


import com.bookstore.jpa.dtos.BookRecordDto;
import com.bookstore.jpa.models.AuthorModel;
import com.bookstore.jpa.models.BookModel;
import com.bookstore.jpa.models.ReviewModel;
import com.bookstore.jpa.repositories.AuthorRepository;
import com.bookstore.jpa.repositories.BookRepository;
import com.bookstore.jpa.repositories.PublisherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {


    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    public List<BookModel> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public BookModel saveBook(BookRecordDto bookRecordDto) {
        if (bookRecordDto.publisherId() == null) {
            throw new IllegalArgumentException("O ID do publisher não pode ser nulo");
        }


        BookModel book = new BookModel();
        book.setTitle(bookRecordDto.title());

        if (bookRecordDto.authorsIds() != null && !bookRecordDto.authorsIds().isEmpty()) {
            // Se não estiver vazia, adiciona os autores
            book.setAuthors(authorRepository.findAllById(bookRecordDto.authorsIds())
                    .stream()
                    .collect(Collectors.toSet()));
        } else {
            // Caso a lista de autores esteja vazia, podemos deixar como uma coleção vazia ou o que for necessário
            book.setAuthors(Collections.emptySet());
        }
        // Configura o Review
        ReviewModel reviewModel = new ReviewModel();
        reviewModel.setComment(bookRecordDto.reviewComment());
        reviewModel.setBook(book);
        book.setReview(reviewModel);

        return bookRepository.save(book);
    }
}