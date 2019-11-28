package com.example.book;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class BookControllerTest {

    private final Book book =
            new Book("CocaCola", "Taschenbuch", "Abassin Saleh", "12.01.2001", "120");
    private final Book bookWrongPublicationDate =
            new Book("PepsiMax", "Wissen", "Abassin Saleh", "99.99.2001", "110");
    private final List<Book> bookList = Arrays.asList(
            new Book("Sprite", "Fantasy", "Abassin Saleh", "01.01.2019", "130"),
            new Book("Fanta", "Sci-Fi", "Abassin Saleh", "01.01.1902", "10"),
            new Book("Cider", "Kochbuch", "Abassin Saleh", "12.12.1902", "40"));
    private final Book emptyBook = new Book(null, "", null, null, null);

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookRepository repository;

    @Test
    void showAllBooks_findAllBooks_returnListOfBooksWithThreeBooks() throws Exception {
        when(repository.findAll()).thenReturn(bookList);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/books")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBooks = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                }
        );
        Assertions.assertIterableEquals(bookList, returnedBooks);
        verify(repository).findAll();
    }

    @Test
    void showAllBooks_findAllBooks_returnEmptyList() throws Exception {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/books")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBooks = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                }
        );
        assertIterableEquals(Collections.emptyList(), returnedBooks);
        verify(repository).findAll();
    }

    @Test
    void createNewBook_saveANewBook_newBookIsSaved() throws Exception {
        when(repository.save(book)).thenReturn(book);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/books")
                        .content(new ObjectMapper().writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        Book returnedBook = new ObjectMapper().readValue(
                mvcResult.getResponse().getContentAsString(),
                Book.class
        );
        assertEquals(book, returnedBook);
        verify(repository).save(book);
    }

    @Test
    void createNewBook_saveANewBook_returnsBadRequest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/books")
                        .content(new ObjectMapper().writeValueAsString(bookWrongPublicationDate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNewBook_saveANewBook_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> repository.save(emptyBook));
    }
}