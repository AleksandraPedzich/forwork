package com.pedzich.aleksandra.library.controllers;

import com.pedzich.aleksandra.library.models.Author;
import com.pedzich.aleksandra.library.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    ResponseEntity<List<Author>> findAll() {
        return ResponseEntity.ok().body(authorService.findAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Author> findById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(authorService.findById(id));
    }

    @PostMapping
    ResponseEntity<Author> save(@RequestBody Author author) {
        authorService.save(author);
        return ResponseEntity.status(201).body(author);
    }

    @PatchMapping(path = "/{id}")
    ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Author author) {
        author.setId(id);
        authorService.update(author);
        return ResponseEntity.status(202).build();
    }

    @DeleteMapping(path = "/{id}")
    ResponseEntity<Void> delete(@PathVariable Integer id) {
        authorService.delete(id);
        return ResponseEntity.status(204).build();
    }
}