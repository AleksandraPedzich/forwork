package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.models.Author;
import com.pedzich.aleksandra.library.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Optional<Author> findById(Integer id) {
        return authorRepository.findById(id);
    }

    public void save(Author author) {
        authorRepository.saveAndFlush(author);
    }

    public void update(Author author) throws EntityNotFoundException {
        Optional<Author> optAuthor = authorRepository.findById(author.getId());
        optAuthor.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Author with this id doesn't exist"));
        authorRepository.saveAndFlush(author);
    }

    public void delete(Integer id) throws EntityNotFoundException {
        Optional<Author> optAuthor = authorRepository.findById(id);
        optAuthor.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Author with this id doesn't exist"));
        authorRepository.delete(optAuthor.get());
        authorRepository.flush();
    }
}
