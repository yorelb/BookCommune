package com.yorelb.book_commune.controller;

import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.BorrowStatus;
import com.yorelb.book_commune.service.BorrowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BorrowController.class)
class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mocking the borrow service
    @MockitoBean
    private BorrowService borrowService;

    @Test
    void testRequestBorrow_Success() throws Exception {
        BorrowRecord mockRecord = new BorrowRecord();
        mockRecord.setId(10L);
        mockRecord.setStatus(BorrowStatus.PENDING);

        when(borrowService.borrowBook(1L, 2L)).thenReturn(mockRecord);

        mockMvc.perform(post("/api/borrow/request")
                        .param("bookId", "1")
                        .param("borrowerId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testRequestBorrow_FailsWhenBorrowingOwnBook() throws Exception {
        when(borrowService.borrowBook(1L, 1L))
                .thenThrow(new IllegalArgumentException("You cannot borrow your own book."));

        mockMvc.perform(post("/api/borrow/request")
                        .param("bookId", "1")
                        .param("borrowerId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("You cannot borrow your own book."));
    }

    @Test
    void testApproveBorrow_Success() throws Exception {
        BorrowRecord mockRecord = new BorrowRecord();
        mockRecord.setId(5L);
        mockRecord.setStatus(BorrowStatus.ACTIVE);

        when(borrowService.approveBorrowRequest(5L)).thenReturn(mockRecord);

        mockMvc.perform(post("/api/borrow/5/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testApproveBorrow_Fail() throws Exception {
        when(borrowService.approveBorrowRequest(8L)).thenThrow(new IllegalArgumentException("Transaction not found."));

        mockMvc.perform(post("/api/borrow/8/approve"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Transaction not found."));
    }
}