package com.example.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ApiModel(description = "Details about the book")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book {

    @Id
    @ApiModelProperty(notes = "The unique _id, which is used to get the Book in the Database")
    private String _id;

    @ApiModelProperty(notes = "The title of the book")
    private String title;
    @ApiModelProperty(notes = "The genre of the book")
    private String genre;
    @ApiModelProperty(notes = "The author of the book")
    private String author;
    @ApiModelProperty(notes = "The date of the book's publication")
    private String publicationDate;
    @ApiModelProperty(notes = "The total number of pages")
    private String pageNumber;


    public Book(String title, String genre, String author,
                String publicationDate, String pageNumber) {
        this.title = title;
        this.genre = genre;
        if ("".equals(author)) {
            this.author = "Unknown";
        } else {
            this.author = author;
        }
        this.publicationDate = publicationDate;
        this.pageNumber = pageNumber;
    }
}
