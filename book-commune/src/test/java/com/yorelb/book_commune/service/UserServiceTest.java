package com.yorelb.book_commune.service;

import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.BorrowStatus;
import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BorrowRecordRepository borrowRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("booklover");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encrypted_password");
        testUser.setCity("London");
    }

    @Test
    void testAddUser_EncryptsPasswordAndSaves() {
        User rawUser = new User();
        rawUser.setPassword("plainTextPass");

        when(passwordEncoder.encode("plainTextPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.addUser(rawUser);

        assertEquals("encodedPass", savedUser.getPassword());
        verify(userRepository, times(1)).save(rawUser);
    }

    @Test
    void testUpdateUser_Success() {
        User updateDetails = new User();
        updateDetails.setUsername("newUsername");
        updateDetails.setCity("Paris");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateUser(1L, updateDetails);

        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals("Paris", updatedUser.getCity());
        assertEquals("test@example.com", updatedUser.getEmail());
    }

    @Test
    void testUpdateUser_FailsIfUsernameTakenBySomeoneElse() {
        User updateDetails = new User();
        updateDetails.setUsername("takenUsername");

        User otherUser = new User();
        otherUser.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("takenUsername")).thenReturn(Optional.of(otherUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, updateDetails));

        assertEquals("That username is already taken. Please choose another one.", exception.getMessage());
    }

    @Test
    void testFindUser_CalculatesBorrowedAndLentStats() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(borrowRepository.findAllByBorrowerIdAndStatusNot(1L, BorrowStatus.REJECTED))
                .thenReturn(List.of(new BorrowRecord(), new BorrowRecord()));
        when(borrowRepository.countByBookOwnerIdAndStatusNot(1L, BorrowStatus.REJECTED))
                .thenReturn(3);

        User foundUser = userService.findUser(1L);

        assertEquals(2, foundUser.getBorrowedBooks());
        assertEquals(3, foundUser.getLentBooks());
    }

    @Test
    void testVerifyLogin_Success() {
        when(userRepository.findByUsername("booklover")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("rawPass", "encrypted_password")).thenReturn(true);

        User loggedIn = userService.verifyLogin("booklover", "rawPass");
        assertNotNull(loggedIn);
    }

    @Test
    void testVerifyLogin_FailsOnWrongPassword() {
        when(userRepository.findByUsername("booklover")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPass", "encrypted_password")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.verifyLogin("booklover", "wrongPass"));
    }

    @Test
    void testChangeEmail_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("myPassword", "encrypted_password")).thenReturn(true);
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updated = userService.changeEmail(1L, "test@example.com", "new@example.com", "myPassword");

        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void testChangeEmail_FailsOnWrongCurrentEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class,
                () -> userService.changeEmail(1L, "wrong@example.com", "new@example.com", "myPass"));
    }

    @Test
    void testChangePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass", "encrypted_password")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncryptedPass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updated = userService.changePassword(1L, "oldPass", "newPass");

        assertEquals("newEncryptedPass", updated.getPassword());
    }
}