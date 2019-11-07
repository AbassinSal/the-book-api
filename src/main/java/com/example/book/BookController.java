package com.example.book;

import org.springframework.web.bind.annotation.*;

@RestController
public class BookController {

    private final BookRepository repository;

    BookController(BookRepository repository) {
        this.repository = repository;
    }
    
    @PostMapping("/books")
    Book createNewBook(@RequestBody Book newBook) {
        return repository.save(newBook);
    }
}
