package com.example.book;

class WrongFormatException extends RuntimeException{
    WrongFormatException() {
        super("Publication Date is not correct formatted (dd.mm.yyyy)");
    }
}
