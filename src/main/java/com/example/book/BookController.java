package com.example.book;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class BookController {


    private final BookRepository repository;

    BookController(BookRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/books")
    Book createNewBook(@RequestBody Book newBook) {
        validatePublicationDate(newBook.getPublicationDate());
        createId(newBook);
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
    List<Book> showSpecificBook(@PathVariable String searchAttribute,
                                @PathVariable String searchString) {

        List<Book> allBooks = repository.findAll();
        List<Book> filteredBooks = new ArrayList<>();

        int i = 0;
        if (searchAttribute.equalsIgnoreCase("title")) {
            for (Book book : allBooks) {
                i++;
                String currentTitle = book.getTitle();
                if (currentTitle.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("title", searchString);
                }
            }
        } else if (searchAttribute.equalsIgnoreCase("genre")) {
            for (Book book : allBooks) {
                i++;
                String currentGenre = book.getGenre();
                if (currentGenre.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("genre", searchString);
                }
            }
        } else if (searchAttribute.equalsIgnoreCase("publicationDate")) {
            validatePublicationDate(searchString);
            for (Book book : allBooks) {
                i++;
                String currentPublicationDate = book.getPublicationDate();
                if (currentPublicationDate.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("publicationDate", searchString);
                }
            }
        } else if (searchAttribute.equalsIgnoreCase("author")) {
            for (Book book : allBooks) {
                i++;
                String currentAuthor = book.getAuthor();
                if (currentAuthor.equalsIgnoreCase(searchString)) {
                    filteredBooks.add(book);
                } else if (i >= allBooks.size() && filteredBooks.isEmpty()) {
                    throw new BookNotFoundException("author", searchString);
                }
            }
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

    private void validatePublicationDate(String searchString) {
        Pattern pattern = Pattern.compile("^([0-2][0-9]|(3)[0-1])(\\.)(((0)[0-9])|((1)[0-2]))(\\.)\\d{4}$");
        Matcher matcher = pattern.matcher(searchString);

        if (!matcher.find()) {
            throw new WrongFormatException();
        }
    }

    private void createId(Book newBook) {
        List<Book> allBooks = repository.findAll();
        ArrayList<Integer> allIds = new ArrayList<>();
        ArrayList<Integer> missingIds = new ArrayList<>();

        for (Book book : allBooks) {
            allIds.add(book.getId());
        }
        for (int i = 0; i < allIds.size(); i++) {
            if (allIds.get(i) != i) {
                missingIds.add(i);
            } else {
                Book lastBook = allBooks.get(allBooks.size()-1);
                int lastBookId = lastBook.getId();
                missingIds.add(lastBookId + 1);
            }
        }
        newBook.setId(missingIds.get(0));
        missingIds.remove(0);
    }

}
