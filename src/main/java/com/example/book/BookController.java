package com.example.book;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class BookController {

    private final BookRepository repository;

    BookController(BookRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/books")
    Book createNewBook(@RequestBody Book newBook) {
        Validator.publicationDateValidation(newBook.getPublicationDate());
        IdHandler.createId(newBook, repository);
        return repository.save(newBook);
    }

    @GetMapping("/books")
    List<Book> showAllBooks() {
        return repository.findAll();
    }

    @GetMapping("/books/size")
    int showNumberOfBooks() {
        return repository.findAll().size();
    }

    @GetMapping("/books/{searchAttribute}/{searchContent}")
    List<Book> showSpecificBook(@PathVariable String searchAttribute, @PathVariable String searchContent) {
        List<Book> allBooks = repository.findAll();
        List<Book> filteredBooks = new ArrayList<>();

        switch (searchAttribute.toLowerCase()) {
            case ("title"):
                for (Book book : allBooks) {
                    if (book.getTitle().equalsIgnoreCase(searchContent)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("title", searchContent);
                }
                break;
            case ("genre"):
                for (Book book : allBooks) {
                    if (book.getGenre().equalsIgnoreCase(searchContent)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("genre", searchContent);
                }
                break;
            case ("publicationdate"):
                Validator.publicationDateValidation(searchContent);
                for (Book book : allBooks) {
                    if (book.getPublicationDate().equalsIgnoreCase(searchContent)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("publicationDate", searchContent);
                }
                break;
            case ("author"):
                for (Book book : allBooks) {
                    if (book.getAuthor().equalsIgnoreCase(searchContent)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("author", searchContent);
                }
                break;
        }
        return filteredBooks;
    }


    @DeleteMapping("/books/{id}")
    void deleteBook(@PathVariable Integer id) {
        repository.deleteById(id);
    }

    @PatchMapping("/books/{id}")
    Book updateBook(@PathVariable Integer id, @RequestBody Book newBook) {
        Book chosenBook;

        if (repository.findById(id).isPresent()) {
            chosenBook = repository.findById(id).get();
            if (newBook.getAuthor() != null && !newBook.getAuthor().isEmpty()) {
                chosenBook.setAuthor(newBook.getAuthor());
            }
            if (newBook.getGenre() != null && !newBook.getGenre().isEmpty()) {
                chosenBook.setGenre(newBook.getGenre());
            }
            if (newBook.getPublicationDate() != null && !newBook.getPublicationDate().isEmpty()) {
                Validator.publicationDateValidation(newBook.getPublicationDate());
                chosenBook.setPublicationDate(newBook.getPublicationDate());
            }
            if (newBook.getTitle() != null && !newBook.getTitle().isEmpty()) {
                chosenBook.setTitle(newBook.getTitle());
            }
            if (newBook.getPageNumber() != null) {
                chosenBook.setPageNumber(newBook.getPageNumber());
            }
        } else {
            throw new BookNotFoundException("id", id.toString());
        }

        return repository.save(chosenBook);
    }

    @PutMapping("/books/{id}")
    Book replaceBook(@PathVariable Integer id, @RequestBody Book newBook) {
        return repository.findById(id).map(
                book -> {
                    book.setTitle(newBook.getTitle());
                    Validator.publicationDateValidation(newBook.getPublicationDate());
                    book.setPublicationDate(newBook.getPublicationDate());
                    book.setGenre(newBook.getGenre());
                    book.setPageNumber(newBook.getPageNumber());
                    book.setAuthor(newBook.getAuthor());
                    return repository.save(book);
                }).orElseGet(() -> {
            newBook.setId(id);
            return repository.save(newBook);
        });
    }

}
