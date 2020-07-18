package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.dto.BookDTO;
import com.pedzich.aleksandra.library.models.Author;
import com.pedzich.aleksandra.library.models.Book;
import com.pedzich.aleksandra.library.models.Category;
import com.pedzich.aleksandra.library.repositories.AuthorRepository;
import com.pedzich.aleksandra.library.repositories.BookRepository;
import com.pedzich.aleksandra.library.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Integer id) {
        return bookRepository.findById(id);
    }

    private List<Category> findCategoriesByIds(List<Integer> categoryIds) throws EntityNotFoundException {
        List<Category> categories = null;
        if (categoryIds != null) {
            categories = categoryIds.stream()
                    .map(id -> {
                        Optional<Category> optCategory = categoryRepository.findById(id);
                        optCategory.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Category with this id doesn't exist"));
                        return optCategory.get();
                    })
                    .collect(Collectors.toList());
        }
        return categories;
    }

    public void save(BookDTO bookDTO) {
        Optional<Author> optAuthor = authorRepository.findById(bookDTO.getAuthorId());
        optAuthor.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Author with this id doesn't exist"));
        List<Category> categories = findCategoriesByIds(bookDTO.getCategoryIds());
        bookRepository.saveAndFlush(new Book(bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getType(), optAuthor.get(), categories));
    }

    public void update(BookDTO bookDTO) throws EntityNotFoundException {
        Optional<Author> optAuthor = authorRepository.findById(bookDTO.getAuthorId());
        optAuthor.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Author with this id doesn't exist"));
        Optional<Book> optBook = bookRepository.findById(bookDTO.getId());
        optBook.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Book with this id doesn't exist"));
        List<Category> categories = findCategoriesByIds(bookDTO.getCategoryIds());
        bookRepository.saveAndFlush(new Book(bookDTO.getId(), bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getType(), optAuthor.get(), categories));
    }

    public void delete(Integer id) throws EntityNotFoundException {
        Optional<Book> optBook = bookRepository.findById(id);
        optBook.orElseThrow(() -> new javax.persistence.EntityNotFoundException("Book with this id doesn't exist"));
        bookRepository.delete(optBook.get());
        bookRepository.flush();
    }
}
