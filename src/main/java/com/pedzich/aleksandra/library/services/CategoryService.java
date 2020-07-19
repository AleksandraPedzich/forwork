package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.models.Category;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<Category> findAll();
    Category findById(Integer id);
    void save(Category category);
    void update(Category category) throws EntityNotFoundException;
    void delete(Integer id) throws EntityNotFoundException;
}
