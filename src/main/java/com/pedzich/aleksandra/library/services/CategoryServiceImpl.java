package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.models.Category;
import com.pedzich.aleksandra.library.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    public void save(Category category) {
        categoryRepository.saveAndFlush(category);
    }

    public void update(Category category) throws EntityNotFoundException {
        Optional<Category> optCategory = categoryRepository.findById(category.getId());
        optCategory.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Category with this id doesn't exist"));
        categoryRepository.saveAndFlush(category);
    }

    public void delete(Integer id) throws EntityNotFoundException {
        Optional<Category> optCategory = categoryRepository.findById(id);
        optCategory.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Category with this id doesn't exist"));
        categoryRepository.delete(optCategory.get());
        categoryRepository.flush();
    }
}
