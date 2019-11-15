package com.example.book;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Book {

    @Id
    private int id;

    private String title;
    private String genre;
    private String author;
    private String publicationDate;
    private Integer pageNumber;


    public Book(String title, String genre, String author,
                String publicationDate, int pageNumber) {
        this.title = title;
        this.genre = genre;
        if (author.equalsIgnoreCase("")) {
            this.author = "Unknown";
        } else {
            this.author = author;
        }
        this.publicationDate = publicationDate;
        this.pageNumber = pageNumber;
    }
}
