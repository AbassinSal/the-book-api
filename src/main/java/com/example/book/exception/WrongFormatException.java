package com.example.book.exception;

public class WrongFormatException extends RuntimeException {

    public WrongFormatException() {
        super("Publication Date is not correct formatted (dd.mm.yyyy)");
    }
}
