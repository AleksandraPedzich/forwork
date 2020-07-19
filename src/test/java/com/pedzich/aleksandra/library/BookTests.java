package com.pedzich.aleksandra.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedzich.aleksandra.library.dto.BookDTO;
import com.pedzich.aleksandra.library.enums.Type;
import com.pedzich.aleksandra.library.models.Author;
import com.pedzich.aleksandra.library.models.Book;
import com.pedzich.aleksandra.library.models.Category;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookTests {

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Value("${application.address}")
    private String applicationAddress;

    @Value("${application.port}")
    private String applicationPort;

    private static Author kingAuthor = new Author("Stephen", "King", "Very famous");
    private static Author schwabAuthor = new Author("Victoria", "Schwab", null);

    private static Category fantasyCategory = new Category("Fantasy", null);
    private static Category scienceFictionCategory = new Category("Science fiction", null);
    private static Category horrorCategory = new Category("Horror", "Scary stories");

    private static BookDTO outsiderBook = new BookDTO("1234", "Outsider", Type.HARDCOVER);
    private static BookDTO carrieBook = new BookDTO("5678", "Carrie", Type.EBOOK);
    private static BookDTO vengefulBook = new BookDTO("3456", "Vengeful", Type.PAPERBACK);
    private static BookDTO bookWithoutTitle = new BookDTO("5555", null, Type.PAPERBACK);
    private static BookDTO badBook = new BookDTO("4444", "Very bad book", Type.PAPERBACK);

    private String createURLWithPort(String uri) {
        return applicationAddress + ":" + applicationPort + uri;
    }

    private ResponseEntity<Book> save(BookDTO book) {
        HttpEntity<BookDTO> entity = new HttpEntity<>(book, headers);
        return restTemplate.exchange(
                createURLWithPort("/books"),
                HttpMethod.POST, entity, Book.class);
    }

    private ResponseEntity<List> findAll() {
        return restTemplate.exchange(
                createURLWithPort("/books"),
                HttpMethod.GET, null, List.class);
    }

    private ResponseEntity<Book> findById(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/books/" + id),
                HttpMethod.GET, null, Book.class);
    }

    private ResponseEntity<Void> update(Integer id, BookDTO book) {
        // Workaround for patch
        HttpClient client = HttpClients.createDefault();
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

        HttpEntity<BookDTO> entity = new HttpEntity<>(book, headers);
        return restTemplate.exchange(
                createURLWithPort("/books/" + id),
                HttpMethod.PATCH, entity, Void.class);
    }

    private ResponseEntity<Void> delete(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/books/" + id),
                HttpMethod.DELETE, null, Void.class);
    }

    private ResponseEntity<Author> saveAuthor(Author author) {
        HttpEntity<Author> entity = new HttpEntity<>(author, headers);
        return restTemplate.exchange(
                createURLWithPort("/authors"),
                HttpMethod.POST, entity, Author.class);
    }

    private ResponseEntity<Void> deleteAuthor(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/authors/" + id),
                HttpMethod.DELETE, null, Void.class);
    }

    private ResponseEntity<Category> saveCategory(Category category) {
        HttpEntity<Category> entity = new HttpEntity<>(category, headers);
        return restTemplate.exchange(
                createURLWithPort("/categories"),
                HttpMethod.POST, entity, Category.class);
    }

    private void compareDTOWithEntity(BookDTO bookDTO, Book book) {
        Assert.assertEquals(bookDTO.getIsbn(), book.getIsbn());
        Assert.assertEquals(bookDTO.getTitle(), book.getTitle());
        Assert.assertEquals(bookDTO.getType(), book.getType());
        Assert.assertEquals(bookDTO.getAuthorId(), book.getAuthor().getId());
        if (book.getCategories() != null && !book.getCategories().isEmpty()) {
            Integer[] categoryIds = book.getCategories().stream()
                    .map(Category::getId)
                    .toArray(Integer[]::new);
            Assert.assertArrayEquals(bookDTO.getCategoryIds().toArray(), categoryIds);
        }
        else
            Assert.assertTrue(bookDTO.getCategoryIds() == null || bookDTO.getCategoryIds().isEmpty());
    }

    @Test
    @Order(1)
    public void saveBook() {
        ResponseEntity<Author> kingResponse = saveAuthor(kingAuthor);
        ResponseEntity<Author> schwabResponse = saveAuthor(schwabAuthor);
        kingAuthor = kingResponse.getBody();
        schwabAuthor = schwabResponse.getBody();

        ResponseEntity<Category> fantasyResponse = saveCategory(fantasyCategory);
        ResponseEntity<Category> scienceFictionResponse = saveCategory(scienceFictionCategory);
        ResponseEntity<Category> horrorResponse = saveCategory(horrorCategory);
        fantasyCategory = fantasyResponse.getBody();
        scienceFictionCategory = scienceFictionResponse.getBody();
        horrorCategory = horrorResponse.getBody();

        outsiderBook.setAuthorId(kingAuthor.getId());
        ResponseEntity<Book> outsiderResponse = save(outsiderBook);
        carrieBook.setAuthorId(kingAuthor.getId());
        carrieBook.setCategoryIds(Arrays.asList(horrorCategory.getId()));
        ResponseEntity<Book> carrieResponse = save(carrieBook);
        vengefulBook.setAuthorId(schwabAuthor.getId());
        vengefulBook.setCategoryIds(Arrays.asList(fantasyCategory.getId(), scienceFictionCategory.getId()));
        ResponseEntity<Book> vengefulResponse = save(vengefulBook);

        Assert.assertTrue(outsiderResponse.getStatusCode().equals(HttpStatus.CREATED)
                && carrieResponse.getStatusCode().equals(HttpStatus.CREATED)
                && vengefulResponse.getStatusCode().equals(HttpStatus.CREATED));

        outsiderBook.setId(outsiderResponse.getBody().getId());
        carrieBook.setId(carrieResponse.getBody().getId());
        vengefulBook.setId(vengefulResponse.getBody().getId());

        compareDTOWithEntity(outsiderBook, outsiderResponse.getBody());
        compareDTOWithEntity(carrieBook, carrieResponse.getBody());
        compareDTOWithEntity(vengefulBook, vengefulResponse.getBody());

        System.out.println("saveBook passed");
    }

    @Test
    @Order(2)
    public void saveBookWithNulls() {
        bookWithoutTitle.setAuthorId(kingAuthor.getId());
        ResponseEntity<Book> withoutNameResponse = save(bookWithoutTitle);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, withoutNameResponse.getStatusCode());
        System.out.println("saveBookWithNulls passed");
    }

    @Test
    @Order(3)
    public void saveBookWithWrongAuthorId() {
        badBook.setAuthorId(1999);
        ResponseEntity<Book> withoutNameResponse = save(badBook);

        Assert.assertEquals(HttpStatus.NOT_FOUND, withoutNameResponse.getStatusCode());
        System.out.println("saveBookWithWrongAuthorId passed");
    }

    @Test
    @Order(4)
    public void saveBookWithWrongCategoryId() {
        badBook.setAuthorId(kingAuthor.getId());
        badBook.setCategoryIds(Arrays.asList(2999));
        ResponseEntity<Book> withoutNameResponse = save(badBook);

        Assert.assertEquals(HttpStatus.NOT_FOUND, withoutNameResponse.getStatusCode());
        System.out.println("saveBookWithWrongCategoryId passed");
    }

    @Test
    @Order(5)
    public void findAllBooks() {
        ResponseEntity<List> allBooksResponse = findAll();
        List<Object> list = allBooksResponse.getBody();

        List<Book> books = list.stream()
                .map(book -> new ObjectMapper().convertValue(book, Book.class))
                .collect(Collectors.toList());

        Assert.assertEquals(books.size(), 3);

        for (Book book: books) {
            switch (book.getTitle()) {
                case "Outsider":
                    compareDTOWithEntity(outsiderBook, book);
                    break;
                case "Carrie":
                    compareDTOWithEntity(carrieBook, book);
                    break;
                default:
                    compareDTOWithEntity(vengefulBook, book);
                    break;
            }
        }

        String[] bookTitles = books.stream()
                .map(Book::getTitle)
                .sorted()
                .toArray(String[]::new);
        Assert.assertArrayEquals(new String[]{"Carrie", "Outsider", "Vengeful"}, bookTitles);

        Assert.assertEquals(HttpStatus.OK, allBooksResponse.getStatusCode());
        System.out.println("findAllBooks passed");
    }


    @Test
    @Order(6)
    public void findBookById() {
        Integer outsiderId = outsiderBook.getId();
        ResponseEntity<Book> outsiderResponse = findById(outsiderId);

        compareDTOWithEntity(outsiderBook, outsiderResponse.getBody());

        Assert.assertEquals(HttpStatus.OK, outsiderResponse.getStatusCode());
        System.out.println("findBookById passed");
    }


    @Test
    @Order(7)
    public void findBookByIdWithWrongId() {
        ResponseEntity<Book> wrongIdResponse = findById(9001);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("findBookByIdWithWrongId passed");
    }

    @Test
    @Order(8)
    public void updateAuthor() {
        Integer outsiderId = outsiderBook.getId();
        outsiderBook = new BookDTO("1111", "Outsider", Type.EBOOK);
        outsiderBook.setAuthorId(schwabAuthor.getId());
        outsiderBook.setCategoryIds(Arrays.asList(horrorCategory.getId()));
        ResponseEntity<Void> outsiderUpdateResponse = update(outsiderId, outsiderBook);

        Assert.assertEquals(HttpStatus.ACCEPTED, outsiderUpdateResponse.getStatusCode());

        ResponseEntity<Book> outsiderResponse = findById(outsiderId);

        compareDTOWithEntity(outsiderBook, outsiderResponse.getBody());

        Assert.assertEquals(HttpStatus.OK, outsiderResponse.getStatusCode());
        System.out.println("updateAuthor passed");
    }

    @Test
    @Order(9)
    public void updateBookWithWrongId() {
        ResponseEntity<Void> wrongIdResponse = update(8999, outsiderBook);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("updateBookWithWrongId passed");
    }

    @Test
    @Order(10)
    public void deleteBook() {
        Integer carrieId = carrieBook.getId();
        ResponseEntity<Void> carrieResponse = delete(carrieId);

        Assert.assertEquals(HttpStatus.NO_CONTENT, carrieResponse.getStatusCode());

        ResponseEntity<Book> carrieAfterDeleteResponse = findById(carrieId);

        Assert.assertEquals(HttpStatus.NOT_FOUND, carrieAfterDeleteResponse.getStatusCode());

        ResponseEntity<List> allBooksResponse = findAll();

        Assert.assertEquals(2, allBooksResponse.getBody().size());

        // Removing author removes with books
        Integer schwabId = schwabAuthor.getId();
        ResponseEntity<Void> schwabResponse = deleteAuthor(schwabId);

        allBooksResponse = findAll();

        Assert.assertEquals(0, allBooksResponse.getBody().size());
        Assert.assertEquals(HttpStatus.OK, allBooksResponse.getStatusCode());
        System.out.println("deleteBook passed");
    }

    @Test
    @Order(11)
    public void deleteBookWithWrongId() {
        ResponseEntity<Book> wrongIdResponse = findById(2341);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("deleteBookWithWrongId passed");
    }
}