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

import java.util.ArrayList;
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
            new Book("Sprite", "Fantasy", "Abassin", "01.01.2019", "130"),
            new Book("Fanta", "Sci-Fi", "Hans Mustermann", "01.01.1902", "10"),
            new Book("Cider", "Kochbuch", "Abassin Saleh", "12.12.1902", "40")
    );
    private final List<Book> genreFilteredList = Arrays.asList(
            new Book("Sprite", "Fantasy", "Abassin", "01.01.2019", "130"),
            new Book("Fanta", "Fantasy", "Abassin", "01.01.2019", "130")
    );
    private final List<Book> authorFilteredList = Arrays.asList(
            new Book("Sprite", "Sci-Fi", "Abass", "01.01.2019", "130"),
            new Book("Fanta", "Sci-Fi", "Abass", "01.01.2019", "130")
    );
    private final List<Book> publicationDateFilteredList = Arrays.asList(
            new Book("Sprite", "Sci-Fi", "Abassin", "02.01.2019", "130"),
            new Book("Fanta", "Sci-Fi", "Abassin", "02.01.2019", "130")
    );
    private final List<Book> titleFilteredList = Arrays.asList(
            new Book("Fanta", "Sci-Fi", "Abassin", "02.01.2019", "130"),
            new Book("Fanta", "Sci-Fi", "Abassin", "02.01.2019", "130")
    );
    private final List<Book> pageNumberFilteredList = Arrays.asList(
            new Book("Fanta", "Sci-Fi", "Abassin", "02.01.2019", "140"),
            new Book("Fanta", "Sci-Fi", "Abassin", "02.01.2019", "140")
    );
    private final List<Book> emptyList = new ArrayList<>();
    private final Book bookWithAuthorAndTitle =
            new Book("Kaffee", null, "Abassin Saleh", null, null);
    private final Book emptyBook =
            new Book(null, null, null, null, null);
    private final Book bookWithoutAuthorAndTitle =
            new Book(null, "Fantasy", null, "20.20.2020", "220");
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookRepository repository;

    @Test
    void showAllBooks_findAllBooks_returnListOfBooksWithThreeBooks() throws Exception {
        when(repository.findAll()).thenReturn(bookList);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/books/all")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBooks = objectMapper.readValue(
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
                MockMvcRequestBuilders
                        .get("/api/books/all")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBooks = objectMapper.readValue(
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
                        .content(objectMapper.writeValueAsString(book))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        Book returnedBook = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                Book.class
        );
        assertEquals(book, returnedBook);
        verify(repository).save(book);
    }

    @Test
    void createNewBook_saveANewBook_returnsBadRequestWrongPublicationDate() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/books")
                        .content(objectMapper.writeValueAsString(bookWrongPublicationDate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createNewBook_saveANewBook_savesBookWithAuthorAndTitle() throws Exception {
        when(repository.save(bookWithAuthorAndTitle)).thenReturn(bookWithAuthorAndTitle);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/books")
                        .content(objectMapper.writeValueAsString(bookWithAuthorAndTitle))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        Book returnedBook = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                Book.class
        );
        assertEquals(bookWithAuthorAndTitle, returnedBook);
    }

    @Test
    void createNewBook_saveANewBook_returnsBadRequestEmptyBook() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/books")
                        .content(objectMapper.writeValueAsString(emptyBook))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createNewBook_saveANewBook_returnsBadRequestBookWithoutAuthorAndTitle() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/books")
                        .content(objectMapper.writeValueAsString(bookWithoutAuthorAndTitle))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void showNumberOfBooks_findAll_returnsAmountOfBooks() throws Exception {
        when(repository.count()).thenReturn(2L);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/books/size")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        long size = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), long.class);

        assertEquals(2L, size);
    }

    @Test
    void deleteBook_deleteById_deletesBook() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void filterBooks_getABookWithGenreFantasy_returnsBookWithGenreFantasy() throws Exception {
        when(repository.findBooksByGenre("Fantasy")).thenReturn(genreFilteredList);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/books?genre=Fantasy")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBooks = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                }
        );
        assertEquals(genreFilteredList, returnedBooks);
    }

    @Test
    void filterBooks_getABookWithAuthorAbassin_returnsBookWithAuthorAbassin() throws Exception {
        when(repository.findBooksByAuthor("Abass")).thenReturn(authorFilteredList);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/books?author=Abass")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBook = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                }
        );
        assertEquals(authorFilteredList, returnedBook);
    }

    @Test
    void filterBooks_getABookWithAuthorNotExistingAuthor_returnsEmptyList() throws Exception {
        when(repository.findBooksByGenre("bread")).thenReturn(emptyList);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/books?author=bread")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBooks = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                }
        );
        assertEquals(emptyList, returnedBooks);

    }

    @Test
    void filterBooks_getABookWithSpecificPublicationDate_returnsBookWithSpecificPublicationDate() throws Exception {
        when(repository.findBooksByPublicationDate("01.01.2019")).thenReturn(publicationDateFilteredList);

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/books?publicationDate=01.01.2019")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        List<Book> returnedBooks = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                }
        );
        assertEquals(publicationDateFilteredList, returnedBooks);
    }

   @Test
   void filterBooks_getABookWithTitleFanta_returnsBookWithTitleFanta() throws Exception {
        when(repository.findBooksByTitle("Fanta")).thenReturn(titleFilteredList);

       MvcResult mvcResult = mockMvc.perform(
               MockMvcRequestBuilders
                       .get("/api/books?title=Fanta")
                       .accept(MediaType.APPLICATION_JSON)
       ).andExpect(status().isOk()).andReturn();

       List<Book> returnedBooks = objectMapper.readValue(
               mvcResult.getResponse().getContentAsString(),
               new TypeReference<List<Book>>() {
               }
       );
       assertEquals(titleFilteredList, returnedBooks);
   }

   @Test
   void filterBooks_getABookWithpageNumber_returnsBookWithPageNumber() throws Exception {
        when(repository.findBooksByPageNumber("140")).thenReturn(pageNumberFilteredList);

       MvcResult mvcResult = mockMvc.perform(
               MockMvcRequestBuilders
                       .get("/api/books?pageNumber=140")
                       .accept(MediaType.APPLICATION_JSON)
       ).andExpect(status().isOk()).andReturn();

       List<Book> returnedBooks = objectMapper.readValue(
               mvcResult.getResponse().getContentAsString(),
               new TypeReference<List<Book>>() {
               }
       );
       assertEquals(pageNumberFilteredList, returnedBooks);
   }

}
