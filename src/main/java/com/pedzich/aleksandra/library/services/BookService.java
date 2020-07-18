package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.dto.BookDTO;
import com.pedzich.aleksandra.library.models.Book;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> findAll();
    Optional<Book> findById(Integer id);
    void save(BookDTO bookDTO);
    void update(BookDTO bookDTO);
    void delete(Integer id) throws EntityNotFoundException;
}
