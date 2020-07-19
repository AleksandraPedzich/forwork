package com.pedzich.aleksandra.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedzich.aleksandra.library.models.Author;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthorTests {

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Value("${application.address}")
    private String applicationAddress;

    @Value("${application.port}")
    private String applicationPort;

    private static Author kingAuthor = new Author("Stephen", "King", "Very famous");
    private static Author schwabAuthor = new Author("Victoria", "Schwab", null);
    private static Author authorWithoutSurname = new Author("Andrew", null, null);

    private String createURLWithPort(String uri) {
        return applicationAddress + ":" + applicationPort + uri;
    }

    private ResponseEntity<Author> save(Author author) {
        HttpEntity<Author> entity = new HttpEntity<>(author, headers);
        return restTemplate.exchange(
                createURLWithPort("/authors"),
                HttpMethod.POST, entity, Author.class);
    }

    private ResponseEntity<List> findAll() {
        return restTemplate.exchange(
                createURLWithPort("/authors"),
                HttpMethod.GET, null, List.class);
    }

    private ResponseEntity<Author> findById(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/authors/" + id),
                HttpMethod.GET, null, Author.class);
    }

    private ResponseEntity<Void> update(Integer id, Author author) {
        // Workaround for patch
        HttpClient client = HttpClients.createDefault();
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

        HttpEntity<Author> entity = new HttpEntity<>(author, headers);
        return restTemplate.exchange(
                createURLWithPort("/authors/" + id),
                HttpMethod.PATCH, entity, Void.class);
    }

    private ResponseEntity<Void> delete(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/authors/" + id),
                HttpMethod.DELETE, null, Void.class);
    }

    @Test
    @Order(1)
    public void saveAuthor() {
        ResponseEntity<Author> kingResponse = save(kingAuthor);
        ResponseEntity<Author> schwabResponse = save(schwabAuthor);
        kingAuthor = kingResponse.getBody();
        schwabAuthor = schwabResponse.getBody();

        Assert.assertTrue(kingResponse.getStatusCode().equals(HttpStatus.CREATED) && schwabResponse.getStatusCode().equals(HttpStatus.CREATED));
        System.out.println("saveAuthor passed");
    }

    @Test
    @Order(2)
    public void saveAuthorWithNulls() {
        ResponseEntity<Author> withoutNameResponse = save(authorWithoutSurname);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, withoutNameResponse.getStatusCode());
        System.out.println("saveAuthorWithNulls passed");
    }

    @Test
    @Order(3)
    public void findAllCategories() {
        ResponseEntity<List> allAuthorsResponse = findAll();
        List<Object> list = allAuthorsResponse.getBody();

        List<Author> categories = list.stream()
                .map(author -> new ObjectMapper().convertValue(author, Author.class))
                .collect(Collectors.toList());

        Assert.assertEquals(categories.size(), 2);

        Long authorCount = categories.stream()
                .filter(author -> author.getName().equals(kingAuthor.getName()))
                .count();
        Long schwabCount = categories.stream()
                .filter(author -> author.getName().equals(schwabAuthor.getName()))
                .count();
        Assert.assertTrue(authorCount.equals(1L) && schwabCount.equals(1L));

        Assert.assertEquals(HttpStatus.OK, allAuthorsResponse.getStatusCode());
        System.out.println("findAllAuthors passed");
    }

    @Test
    @Order(4)
    public void findAuthorById() {
        Integer kingId = kingAuthor.getId();
        Integer schwabId = schwabAuthor.getId();
        ResponseEntity<Author> kingResponse = findById(kingId);
        ResponseEntity<Author> schwabResponse = findById(schwabId);

        Assert.assertEquals(kingAuthor, kingResponse.getBody());
        Assert.assertEquals(schwabAuthor, schwabResponse.getBody());

        Assert.assertEquals(HttpStatus.OK, kingResponse.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, schwabResponse.getStatusCode());
        System.out.println("findAuthorById passed");
    }

    @Test
    @Order(5)
    public void findAuthorByIdWithWrongId() {
        ResponseEntity<Author> wrongIdResponse = findById(9001);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("findAuthorByIdWithWrongId passed");
    }

    @Test
    @Order(6)
    public void updateAuthor() {
        Integer kingId = kingAuthor.getId();
        kingAuthor = new Author("Stephen Edwin", "King", "So so so very famous");
        kingAuthor.setId(kingId);
        ResponseEntity<Void> kingUpdateResponse = update(kingId, kingAuthor);

        Assert.assertEquals(HttpStatus.ACCEPTED, kingUpdateResponse.getStatusCode());

        ResponseEntity<Author> kingResponse = findById(kingId);

        Assert.assertEquals(kingAuthor, kingResponse.getBody());

        Assert.assertEquals(HttpStatus.OK, kingResponse.getStatusCode());
        System.out.println("updateAuthor passed");
    }

    @Test
    @Order(7)
    public void updateAuthorWithWrongId() {
        ResponseEntity<Void> wrongIdResponse = update(9001, kingAuthor);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("updateAuthorWithWrongId passed");
    }

    @Test
    @Order(8)
    public void deleteAuthor() {
        Integer kingId = kingAuthor.getId();
        Integer schwabId = schwabAuthor.getId();

        ResponseEntity<Void> kingResponse = delete(kingId);
        ResponseEntity<Void> schwabResponse = delete(schwabId);

        Assert.assertEquals(HttpStatus.NO_CONTENT, kingResponse.getStatusCode());
        Assert.assertEquals(HttpStatus.NO_CONTENT, schwabResponse.getStatusCode());

        ResponseEntity<List> allCategoriesResponse = findAll();

        Assert.assertEquals(0, allCategoriesResponse.getBody().size());
        Assert.assertEquals(HttpStatus.OK, allCategoriesResponse.getStatusCode());
        System.out.println("deleteAuthor passed");
    }

    @Test
    @Order(9)
    public void deleteAuthorWithWrongId() {
        ResponseEntity<Void> wrongIdResponse = delete(9001);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("deleteAuthorWithWrongId passed");
    }
}