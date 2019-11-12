package com.example.book;

import java.util.ArrayList;
import java.util.List;

abstract class IdHandler {
    static void createId(Book newBook, BookRepository repository) {
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
                Book lastBook = allBooks.get(allBooks.size() - 1);
                int lastBookId = lastBook.getId();
                missingIds.add(lastBookId + 1);
            }
        }
        newBook.setId(missingIds.get(0));
        missingIds.remove(0);
    }
}
