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

    @GetMapping("/books")
    List<Book> showAllBooks() {
        return repository.findAll();
    }

    @GetMapping("/books/size")
    int showNumberOfBooks() {
        return repository.findAll().size();
    }

    @GetMapping("/books/{searchAttribute}/{searchString}")
    List<Book> showSpecificBook(@PathVariable("searchAttribute") String searchAttribute,
                                @PathVariable("searchString") String searchString) {

        List<Book> allBooks = repository.findAll();
        List<Book> filteredBooks = new ArrayList<>();

        int i = 0;

        if (searchAttribute.equalsIgnoreCase("title")) {
            for (Book book : allBooks) {
                i++;
                String currentTitle = book.getTitle();
                if (currentTitle.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() - 1 && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException(searchString);
                }
            }
        } else if (searchAttribute.equalsIgnoreCase("genre")) {
            for (Book book : allBooks) {
                i++;
                String currentGenre = book.getGenre();
                if (currentGenre.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() - 1 && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException(searchString);
                }
            }
        } else if (searchAttribute.equalsIgnoreCase("publicationDate")) {
            for (Book book : allBooks) {
                i++;
                String currentPublicationDate = book.getPublicationDate();
                if (currentPublicationDate.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() - 1 && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException(searchString);
                }
            }
        } else if (searchAttribute.equalsIgnoreCase("author")) {
            for (Book book : allBooks) {
                i++;
                String currentAuthor= book.getAuthor();
                if (currentAuthor.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() - 1 && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException(searchString);
                }
            }
        }
        return filteredBooks;
    }
}

