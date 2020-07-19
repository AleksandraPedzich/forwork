package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.models.Author;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface AuthorService {

    List<Author> findAll();
    Author findById(Integer id);
    void save(Author author);
    void update(Author author) throws EntityNotFoundException;
    void delete(Integer id) throws EntityNotFoundException;
}
