package com.example.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
        Book book = new Book(newBook);
        try {
            Validator.publicationDateValidation(book.getPublicationDate());
        } catch (WrongFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Publication Date is not correct formatted (dd.mm.yyyy)");
        }
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ObjectMapper().writeValueAsString(repository.save(book)));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not parse book as json");
        }
    }

    @GetMapping("/books")
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
    public int showNumberOfBooks() {
        return repository.findAll().size();
    }


    @GetMapping("/books/{searchAttribute}/{searchContent}")
    @ApiOperation(
            value = "Filter through all books",
            notes = "Filtering through books with help of two filtering Options," +
                    "returning a List of Books with same properties",
            response = Book.class,
            responseContainer = "List"
    )
    public List<Book> showSpecificBook(
            @ApiParam(value = "An attribute of a book that you're looking for", required = true)
            @PathVariable String searchAttribute,
            @ApiParam(value = "The content of the attribute you're looking for", required = true)
            @PathVariable String searchContent) {
        List<Book> allBooks = repository.findAll();
        List<Book> filteredDatabaseBooks = new ArrayList<>();

        switch (searchAttribute.toLowerCase()) {
            case ("title"):
                for (Book book : allBooks) {
                    if (book.getTitle().equalsIgnoreCase(searchContent)) {
                        filteredDatabaseBooks.add(book);
                    }
                }
                if (filteredDatabaseBooks.isEmpty()) {
                    throw new BookNotFoundException("title", searchContent);
                }
                break;
            case ("genre"):
                for (Book book : allBooks) {
                    if (book.getGenre().equalsIgnoreCase(searchContent)) {
                        filteredDatabaseBooks.add(book);
                    }
                }
                if (filteredDatabaseBooks.isEmpty()) {
                    throw new BookNotFoundException("genre", searchContent);
                }
                break;
            case ("publicationdate"):
                Validator.publicationDateValidation(searchContent);
                for (Book book : allBooks) {
                    if (book.getPublicationDate().equalsIgnoreCase(searchContent)) {
                        filteredDatabaseBooks.add(book);
                    }
                }
                if (filteredDatabaseBooks.isEmpty()) {
                    throw new BookNotFoundException("publicationDate", searchContent);
                }
                break;
            case ("author"):
                for (Book book : allBooks) {
                    if (book.getAuthor().equalsIgnoreCase(searchContent)) {
                        filteredDatabaseBooks.add(book);
                    }
                }
                if (filteredDatabaseBooks.isEmpty()) {
                    throw new BookNotFoundException("author", searchContent);
                }
                break;
        }
        return filteredDatabaseBooks;
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
