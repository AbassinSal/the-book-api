package com.example.book;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookRepository repository;

    BookController(BookRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/books")
    @ApiOperation(
            value = "Adds a Book to the database",
            notes = "Creates an object of Type book with the ",
            response = Book.class)
    Book createNewBook(
            @ApiParam(value = "An Object of type Book that you want to add to the Database", required = true)
            @RequestBody Book newBook) {
        Validator.publicationDateValidation(newBook.getPublicationDate());
        IdHandler.createId(newBook, repository);
        return repository.save(newBook);
    }

    @GetMapping("/books")
    @ApiOperation(
            value = "Shows all books",
            notes = "Provides a List of type Book with all books saved in the DB",
            response = Book.class,
            responseContainer = "List")
    List<Book> showAllBooks() {
        return repository.findAll();
    }

    @GetMapping("/books/size")
    @ApiOperation(
            value = "Shows the total amount of books",
            notes = "Provides the total amount of elements saved in the DB",
            response = int.class
    )
    int showNumberOfBooks() {
        return repository.findAll().size();
    }

    @GetMapping("/books/{searchAttribute}/{searchString}")
    @ApiOperation(
            value = "Filter through all books",
            notes = "Filtering through books with help of two filtering Options, returning a List of Books with same properties",
            response = Book.class,
            responseContainer = "List"
    )
    List<Book> showSpecificBook(
            @ApiParam(value = "An attribute of a book that you're looking for", required = true)
            @PathVariable String searchAttribute,
            @ApiParam(value = "The content of the attribute you're looking for", required = true)
            @PathVariable String searchContent) {
        List<Book> allBooks = repository.findAll();
        List<Book> filteredBooks = new ArrayList<>();

        switch (searchAttribute.toLowerCase()) {
            case ("title"):
                for (Book book : allBooks) {
                    if (book.getTitle().equalsIgnoreCase(searchString)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("title", searchString);
                }
            case ("genre"):
                for (Book book : allBooks) {
                    if (book.getGenre().equalsIgnoreCase(searchString)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("genre", searchString);
                }
            case ("publicationdate"):
                Validator.publicationDateValidation(searchString);
                for (Book book : allBooks) {
                    if (book.getPublicationDate().equalsIgnoreCase(searchString)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("publicationDate", searchString);
                }
            case ("author"):
                for (Book book : allBooks) {
                    if (book.getAuthor().equalsIgnoreCase(searchString)) {
                        filteredBooks.add(book);
                    }
                }
                if (filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("author", searchString);
                }
        }
        return filteredBooks;
    }


    @DeleteMapping("/books/{id}")
    @ApiOperation(
            value = "Deletes a book",
            notes = "An object of type book is deleted by handing over an id",
            response = void.class
    )
    void deleteBook(
            @ApiParam(value = "An id that's used to identify a book in the Database")
            @PathVariable Integer id) {
        repository.deleteById(id);
    }

    @PatchMapping("/books/{id}")
    @ApiOperation(
            value = "Patches attribute of a book",
            notes = "An Object of type book is patched by handing over a RequestBody of type Book and an id",
            response = Book.class
    )
    Book updateBook(
            @ApiParam(value = "An id that's used to identify a book in the Database") @PathVariable Integer id,
            @ApiParam(value = "An Object of type Book that you want to add to the Database") @RequestBody Book newBook) {
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
    @ApiOperation(
            value = "Replaces a book",
            notes = "The id is used to identify a book in the database, which you want to replace with the new book",
            response = Book.class
    )
    Book replaceBook(
            @ApiParam(value = "An id that's used to identify a book in the Database") @PathVariable Integer id,
            @ApiParam(value = "An Object of type Book that you want to add to the Database") @RequestBody Book newBook) {
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
