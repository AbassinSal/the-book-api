package com.example.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Slf4j
@RestController
@RequestMapping("/api")
public class BookController {

    private final BookRepository repository;

    private BookController(BookRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/books")
    @ApiOperation(
            value = "Creates a new book",
            notes = "Creates an object of Type book which is added to the Database",
            response = Book.class)
    public ResponseEntity<String> createNewBook(
            @ApiParam(value = "An Object of type Book that you want to add to the Database", required = true)
            @RequestBody Book newBook) {
        if (newBook.getTitle() == null || newBook.getAuthor() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Author or Title is missing");
        }
        try {
            Validator.publicationDateValidation(newBook.getPublicationDate());
        } catch (WrongFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Publication Date is not correct formatted (dd.mm.yyyy)");
        }
        try {
            Book testBook = repository.save(newBook);
            String test = new ObjectMapper().writeValueAsString(testBook);
            return ResponseEntity.status(HttpStatus.CREATED).body(test);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not parse book as json");
        }
    }

    @GetMapping("/books/all")
    @ApiOperation(
            value = "Shows all books",
            notes = "Provides a List of type Book with all books saved in the Database",
            response = Book.class,
            responseContainer = "List")
    public List<Book> showAllBooks() {
        return repository.findAll();
    }

    @GetMapping("/books/size")
    @ApiOperation(
            value = "Shows the total amount of books",
            notes = "Provides the total amount of elements saved in the Database"
    )
    public Long showNumberOfBooks() {
        return repository.count();
    }


    @GetMapping("/books")
    @ApiOperation(
            value = "Filter through all genres",
            notes = "Filters all book genres," +
                    "returning a List of Books with the same genre",
            response = Book.class,
            responseContainer = "List"
    )

    public List<Book> filterBooks(@RequestParam(required = false) Optional<String> author,
                                  @RequestParam(required = false) Optional<String> genre,
                                  @RequestParam(required = false) Optional<String> title,
                                  @RequestParam(required = false) Optional<String> publicationDate,
                                  @RequestParam(required = false) Optional<String> pageNumber) {
        List<Book> filteredBooks = new ArrayList<>();
        if (author.isPresent()) {
            filteredBooks.addAll(repository.findBooksByAuthor(author.get()));
        } else if (genre.isPresent()) {
            filteredBooks.addAll(repository.findBooksByGenre(genre.get()));
        } else if (title.isPresent()) {
            filteredBooks.addAll(repository.findBooksByTitle(title.get()));
        } else if (publicationDate.isPresent()) {
            filteredBooks.addAll(repository.findBooksByPublicationDate(publicationDate.get()));
        } else pageNumber.ifPresent(e -> filteredBooks.addAll(repository.findBooksByPageNumber(e)));
        return filteredBooks;
    }

    @DeleteMapping("/books/{_id}")
    @ApiOperation(
            value = "Deletes a book",
            notes = "An object of type book is deleted by handing over an _id"
    )
    public void deleteBook(
            @ApiParam(value = "An _id that's used to identify a book in the Database")
            @PathVariable String _id) {
        repository.deleteById(_id);
    }
}
