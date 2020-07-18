package com.pedzich.aleksandra.library.controllers;

import com.pedzich.aleksandra.library.models.Category;
import com.pedzich.aleksandra.library.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping
    ResponseEntity<List<Category>> findAll() {
        return ResponseEntity.ok().body(categoryService.findAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Category> findById(@PathVariable Integer id) {
        Optional<Category> category = categoryService.findById(id);
        return category.map(b -> ResponseEntity.ok().body(b))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<Void> save(@RequestBody Category category) {
        categoryService.save(category);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping(path = "/{id}")
    ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Category category) {
        category.setId(id);
        categoryService.update(category);
        return ResponseEntity.status(202).build();
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.status(204).build();
    }
}