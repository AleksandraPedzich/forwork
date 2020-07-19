package com.pedzich.aleksandra.library.services;

import com.pedzich.aleksandra.library.dto.BookDTO;
import com.pedzich.aleksandra.library.models.Author;
import com.pedzich.aleksandra.library.models.Book;
import com.pedzich.aleksandra.library.models.Category;
import com.pedzich.aleksandra.library.repositories.AuthorRepository;
import com.pedzich.aleksandra.library.repositories.BookRepository;
import com.pedzich.aleksandra.library.repositories.CategoryRepository;
import com.pedzich.aleksandra.library.util.StringUtil;
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

    private Author findAuthorById(Integer id) {
        Optional<Author> optAuthor = authorRepository.findById(id);
        optAuthor.orElseThrow(() ->
                new javax.persistence.EntityNotFoundException(StringUtil.getEntityNotFoundExceptionMessage("Author", id)));
        return optAuthor.get();
    }

    private Book findBookById(Integer id) {
        Optional<Book> optBook = bookRepository.findById(id);
        optBook.orElseThrow(() ->
                new javax.persistence.EntityNotFoundException(StringUtil.getEntityNotFoundExceptionMessage("Book", id)));
        return optBook.get();
    }

    private Category findCategoryById(Integer id) {
        Optional<Category> optCategory = categoryRepository.findById(id);
        optCategory.orElseThrow(() ->
                new javax.persistence.EntityNotFoundException(StringUtil.getEntityNotFoundExceptionMessage("Category", id)));
        return optCategory.get();
    }

    private List<Category> findCategoriesByIds(List<Integer> categoryIds) throws EntityNotFoundException {
        List<Category> categories = null;
        if (categoryIds != null) {
            categories = categoryIds.stream()
                    .map(id -> findCategoryById(id))
                    .collect(Collectors.toList());
        }
        return categories;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Integer id) {
        return findBookById(id);
    }

    public void save(BookDTO bookDTO) {
        Author author = findAuthorById(bookDTO.getAuthorId());
        List<Category> categories = findCategoriesByIds(bookDTO.getCategoryIds());
        bookRepository.saveAndFlush(new Book(bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getType(), author, categories));
    }

    public void update(BookDTO bookDTO) throws EntityNotFoundException {
        Author author = findAuthorById(bookDTO.getAuthorId());
        findBookById(bookDTO.getId());
        List<Category> categories = findCategoriesByIds(bookDTO.getCategoryIds());
        bookRepository.saveAndFlush(new Book(bookDTO.getId(), bookDTO.getIsbn(), bookDTO.getTitle(), bookDTO.getType(), author, categories));
    }

    public void delete(Integer id) throws EntityNotFoundException {
        bookRepository.delete(findBookById(id));
        bookRepository.flush();
    }
}
