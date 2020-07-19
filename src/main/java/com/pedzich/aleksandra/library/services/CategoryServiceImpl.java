package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.models.Author;
import com.pedzich.aleksandra.library.models.Category;
import com.pedzich.aleksandra.library.repositories.CategoryRepository;
import com.pedzich.aleksandra.library.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category findCategoryById(Integer id) {
        Optional<Category> optCategory = categoryRepository.findById(id);
        optCategory.orElseThrow(() ->
                new javax.persistence.EntityNotFoundException(StringUtil.getEntityNotFoundExceptionMessage("Category", id)));
        return optCategory.get();
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Integer id) {
        return findCategoryById(id);
    }

    public void save(Category category) {
        categoryRepository.saveAndFlush(category);
    }

    public void update(Category category) throws EntityNotFoundException {
        findCategoryById(category.getId());
        categoryRepository.saveAndFlush(category);
    }

    public void delete(Integer id) throws EntityNotFoundException {
        categoryRepository.delete(findCategoryById(id));
        categoryRepository.flush();
    }
}
