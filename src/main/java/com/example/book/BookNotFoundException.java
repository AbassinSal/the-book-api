package com.example.book;

class BookNotFoundException extends RuntimeException{
    BookNotFoundException(String attribute, String content) {
        super("Could not find book with "+ attribute + ": " + content);
    }
}
