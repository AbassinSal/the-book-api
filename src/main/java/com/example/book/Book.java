package com.example.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@ApiModel(description = "Details about the book")
@Data
@NoArgsConstructor
class Book {

    @Id
    @ApiModelProperty(notes = "The unique _id, which is used to get the Book in the Database")
    private String _id;


    @JsonProperty(required = true)
    @ApiModelProperty(notes = "The title of the book")
    private String title;

    @JsonProperty(defaultValue = "Not specified")
    @ApiModelProperty(notes = "The genre of the book")
    private String genre;

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "The author of the book")
    private String author;

    @JsonProperty(defaultValue = "Not specified")
    @ApiModelProperty(notes = "The date of the book's publication")
    private String publicationDate;

    @JsonProperty(value = "pageNumber", defaultValue = "not specified")
    @ApiModelProperty(notes = "The total number of pages")
    private String pageNumber;

    Book(String title, String genre, String author, String publicationDate, String pageNumber) {
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.publicationDate = publicationDate;
        this.pageNumber = pageNumber;
    }

    Book(Book book) {
        this(book.getTitle(), book.getGenre(), book.getAuthor(), book.getPublicationDate(), book.getPageNumber());
    }
}
