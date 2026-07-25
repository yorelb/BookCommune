package com.yorelb.book_commune.repository;
import com.yorelb.book_commune.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByOwnerId(Long ownerId);
}

//Dont forget jparepo methods:
//.save(entity)
//.findAll()
//.findById(id)
//.deleteById(id)
