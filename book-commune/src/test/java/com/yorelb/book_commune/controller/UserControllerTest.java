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
                    "name": "fanatic",
                    "email": "fanatic@example.com"
                }
                """;

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("fanatic");
        savedUser.setEmail("fanatic@example.com");

        when(userService.addUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("fanatic"))
                .andExpect(jsonPath("$.email").value("fanatic@example.com"));
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("booky");

        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("booky"));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setName("gnome");

        when(userService.findUser(5L)).thenReturn(user);

        mockMvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("gnome"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.findUser(99L)).thenThrow(new IllegalArgumentException("User not found."));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found."));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        String updateInfoJson = """
                {
                    "name": "newName",
                    "email": "new_email@example.com"
                }
                """;

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("newName");
        updatedUser.setEmail("new_email@example.com");

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateInfoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("newName"))
                .andExpect(jsonPath("$.email").value("new_email@example.com"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Cannot delete: User not found."))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot delete: User not found."));
    }
}
