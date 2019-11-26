package com.example.book;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class BookController {

    private final BookRepository repository;

    BookController(BookRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/books")
    @ApiOperation(
            value = "Creates a new book",
            notes = "Creates an object of Type book which is added to the Database",
            response = Book.class)
    Book createNewBook(
            @ApiParam(value = "An Object of type Book that you want to add to the Database", required = true)
            @RequestBody Book newBook) {
        Validator.publicationDateValidation(newBook.getPublicationDate());
        return repository.save(newBook);
    }

    @GetMapping("/books")
    @ApiOperation(
            value = "Shows all books",
            notes = "Provides a List of type Book with all books saved in the Database",
            response = Book.class,
            responseContainer = "List")
    List<Book> showAllBooks() {
        return repository.findAll();
    }

    @GetMapping("/books/size")
    @ApiOperation(
            value = "Shows the total amount of books",
            notes = "Provides the total amount of elements saved in the Database"
    )
    int showNumberOfBooks() {
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
    List<Book> showSpecificBook(
            @ApiParam(value = "An attribute of a book that you're looking for", required = true)
            @PathVariable String searchAttribute,
            @ApiParam(value = "The content of the attribute you're looking for", required = true)
            @PathVariable String searchContent) {
        List<Book> allBooks = repository.findAll();
        List<Book> filtereDatabaseooks = new ArrayList<>();

        switch (searchAttribute.toLowerCase()) {
            case ("title"):
                for (Book book : allBooks) {
                    if (book.getTitle().equalsIgnoreCase(searchContent)) {
                        filtereDatabaseooks.add(book);
                    }
                }
                if (filtereDatabaseooks.isEmpty()) {
                    throw new BookNotFoundException("title", searchContent);
                }
                break;
            case ("genre"):
                for (Book book : allBooks) {
                    if (book.getGenre().equalsIgnoreCase(searchContent)) {
                        filtereDatabaseooks.add(book);
                    }
                }
                if (filtereDatabaseooks.isEmpty()) {
                    throw new BookNotFoundException("genre", searchContent);
                }
                break;
            case ("publicationdate"):
                Validator.publicationDateValidation(searchContent);
                for (Book book : allBooks) {
                    if (book.getPublicationDate().equalsIgnoreCase(searchContent)) {
                        filtereDatabaseooks.add(book);
                    }
                }
                if (filtereDatabaseooks.isEmpty()) {
                    throw new BookNotFoundException("publicationDate", searchContent);
                }
                break;
            case ("author"):
                for (Book book : allBooks) {
                    if (book.getAuthor().equalsIgnoreCase(searchContent)) {
                        filtereDatabaseooks.add(book);
                    }
                }
                if (filtereDatabaseooks.isEmpty()) {
                    throw new BookNotFoundException("author", searchContent);
                }
                break;
        }
        return filtereDatabaseooks;
    }

    @DeleteMapping("/books/{_id}")
    @ApiOperation(
            value = "Deletes a book",
            notes = "An object of type book is deleted by handing over an _id"
    )
    void deleteBook(
            @ApiParam(value = "An _id that's used to identify a book in the Database")
            @PathVariable String id) {
        repository.deleteById(id);
    }
}
