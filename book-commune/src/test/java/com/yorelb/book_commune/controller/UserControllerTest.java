package com.yorelb.book_commune.controller;

import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.service.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void testAddUser_Success() throws Exception {
        String newUserJson = """
            {
                "forename": "book",
                "surname": "fanatic",
                "email": "fanatic@example.com"
            }
            """;

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setForename("book");
        savedUser.setSurname("fanatic");
        savedUser.setEmail("fanatic@example.com");

        when(userService.addUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("book fanatic"))
                .andExpect(jsonPath("$.email").value("fanatic@example.com"));
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setForename("booky");
        user.setSurname("fanatic");

        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("booky fanatic"));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setForename("gnome");
        user.setSurname("juliet");

        when(userService.findUser(5L)).thenReturn(user);

        mockMvc.perform(get("/api/users/5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("gnome juliet"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.findUser(99L)).thenThrow(new IllegalArgumentException("User not found."));

        mockMvc.perform(get("/api/users/99"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found."));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        String updateInfoJson = """
            {
                "forename": "newName",
                "surname": "secondNewName",
                "email": "new_email@example.com"
            }
            """;

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setForename("newName");
        updatedUser.setSurname("secondNewName");
        updatedUser.setEmail("new_email@example.com");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateInfoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName secondNewName"))
                .andExpect(jsonPath("$.email").value("new_email@example.com"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Cannot delete: User not found."))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot delete: User not found."));
    }

    @Test
    void testLogin_Success() throws Exception {
        String loginJson = """
            {
                "username": "booky123",
                "password": "password123"
            }
            """;

        User loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setUsername("booky123");

        when(userService.verifyLogin("booky123", "password123")).thenReturn(loggedInUser);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("booky123"));
    }

    @Test
    void testLogin_Failure() throws Exception {
        String loginJson = """
            {
                "username": "booky123",
                "password": "wrongpassword"
            }
            """;

        when(userService.verifyLogin("booky123", "wrongpassword"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testChangeEmail_Success() throws Exception {
        String emailChangeJson = """
            {
                "currentEmail": "old@example.com",
                "newEmail": "new@example.com",
                "password": "securepassword"
            }
            """;

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("new@example.com");

        when(userService.changeEmail(eq(1L), eq("old@example.com"), eq("new@example.com"), eq("securepassword")))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailChangeJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        String passwordChangeJson = """
            {
                "currentPassword": "oldpassword",
                "newPassword": "newpassword"
            }
            """;

        User updatedUser = new User();
        updatedUser.setId(1L);

        when(userService.changePassword(eq(1L), eq("oldpassword"), eq("newpassword")))
                .thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwordChangeJson))
                .andDo(print())
                .andExpect(status().isOk());
    }
}