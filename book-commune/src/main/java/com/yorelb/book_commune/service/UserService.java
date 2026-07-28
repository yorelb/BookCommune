package com.yorelb.book_commune.service;

import com.yorelb.book_commune.model.Book;
import com.yorelb.book_commune.model.BorrowRecord;
import com.yorelb.book_commune.model.User;
import com.yorelb.book_commune.repository.BookRepository;
import com.yorelb.book_commune.repository.BorrowRecordRepository;
import com.yorelb.book_commune.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class UserService {

    private final BorrowRecordRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserService(BorrowRecordRepository borrowRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    //Add a user to the db
    public User addUser (User newUser) {
       return userRepository.save(newUser);
    }

    //Edit user details
    public User updateUser (Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Can't update: User no found"));
        //Decide on what should be editable
        return userRepository.save(existingUser);
    }

    //Delete a user
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("This user does not exist.");
        }
        userRepository.deleteById(userId);
    }

    //Find a user by id
    public User findUser(Long userId) {
        Optional<User> foundUser =  userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new IllegalArgumentException("Cannot find this user.");
        }
        return foundUser.get();
    }

    //Find all users
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
