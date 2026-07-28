package com.yorelb.book_commune.controller;

import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    // Creating Books
    @Test
    void testAddBook_Success() throws Exception {
        String newBookJson = """
                {
                    "title": "FakeBook",
                    "author": "Frank Castle"
                }
                """;

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("FakeBook");
        savedBook.setAuthor("Frank Castle");
        savedBook.setAvailability(true);
        when(bookService.addBook(any(Book.class))).thenReturn(savedBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("FakeBook"))
                .andExpect(jsonPath("$.availability").value(true));
    }

    // Get all books
    @Test
    void testGetAllBooks_Success() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("1984");

        when(bookService.findAllAvailableBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("1984"));
    }

    // Find book by id
    @Test
    void testGetBookById_Success() throws Exception {
        Book book = new Book();
        book.setId(5L);
        book.setTitle("LOTM");

        when(bookService.findBook(5L)).thenReturn(book);

        mockMvc.perform(get("/api/books/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("LOTM"));
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        when(bookService.findBook(99L)).thenThrow(new IllegalArgumentException("Book not found."));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Book not found."));
    }

    // Find by owner
    @Test
    void testGetBooksByOwner_Success() throws Exception {
        Book book = new Book();
        book.setId(10L);
        book.setTitle("Dune");

        when(bookService.findAllBy(2L)).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books/owner/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dune"));
    }

    // Update book
    @Test
    void testUpdateBook_Success() throws Exception {
        String updateInfoJson = """
                {
                    "title": "NewTitle"
                }
                """;

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("NewTitle");

        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateInfoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("NewTitle"));
    }

    // Delete Book
    @Test
    void testDeleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteBook_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Cannot delete: Book not found."))
                .when(bookService).deleteBook(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot delete: Book not found."));
    }
}