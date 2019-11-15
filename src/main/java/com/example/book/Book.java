package com.example.book;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@ApiModel(description = "Details about the book")
@Data
public class Book {

    @Id
    @ApiModelProperty(notes = "The unique id, which is used to get the Book in the Database")
    private int id;

    @ApiModelProperty(notes = "The title of the book")
    private String title;
    @ApiModelProperty(notes = "The genre of the book")
    private String genre;
    @ApiModelProperty(notes = "The author of the book")
    private String author;
    @ApiModelProperty(notes = "The date of the book's publication")
    private String publicationDate;
    @ApiModelProperty(notes = "The total number of pages")
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
