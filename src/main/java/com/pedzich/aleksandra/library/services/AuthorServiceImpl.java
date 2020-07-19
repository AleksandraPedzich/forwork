package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.models.Author;
import com.pedzich.aleksandra.library.repositories.AuthorRepository;
import com.pedzich.aleksandra.library.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    private Author findAuthorById(Integer id) {
        Optional<Author> optAuthor = authorRepository.findById(id);
        optAuthor.orElseThrow(() ->
                new javax.persistence.EntityNotFoundException(StringUtil.getEntityNotFoundExceptionMessage("Author", id)));
        return optAuthor.get();
    }

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
        findAuthorById(author.getId());
        authorRepository.saveAndFlush(author);
    }

    public void delete(Integer id) throws EntityNotFoundException {
        authorRepository.delete(findAuthorById(id));
        authorRepository.flush();
    }
}
