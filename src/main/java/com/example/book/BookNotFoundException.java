package com.example.book;

class BookNotFoundException extends RuntimeException{
    BookNotFoundException(String type, String attribute) {
        super("Could not find book with  "+ type + ": " + attribute);
    }
}
