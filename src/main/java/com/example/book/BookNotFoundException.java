package com.example.book;

public class BookNotFoundException extends RuntimeException{
    public BookNotFoundException(String attribute) {
        super("Could not find employee " + attribute);
    }
}
