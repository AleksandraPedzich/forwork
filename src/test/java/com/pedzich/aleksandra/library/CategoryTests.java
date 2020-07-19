package com.pedzich.aleksandra.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedzich.aleksandra.library.models.Category;
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
public class CategoryTests {

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Value("${application.address}")
    private String applicationAddress;

    @Value("${application.port}")
    private String applicationPort;

    private static Category fantasyCategory = new Category("Fantasy", null);
    private static Category romanceCategory = new Category("Romance", "Love stories");
    private static Category categoryWithoutName = new Category(null, "Category without a name");

    private String createURLWithPort(String uri) {
        return applicationAddress + ":" + applicationPort + uri;
    }

    private ResponseEntity<Category> save(Category category) {
        HttpEntity<Category> entity = new HttpEntity<>(category, headers);
        return restTemplate.exchange(
                createURLWithPort("/categories"),
                HttpMethod.POST, entity, Category.class);
    }

    private ResponseEntity<List> findAll() {
        return restTemplate.exchange(
                createURLWithPort("/categories"),
                HttpMethod.GET, null, List.class);
    }

    private ResponseEntity<Category> findById(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/categories/" + id),
                HttpMethod.GET, null, Category.class);
    }

    private ResponseEntity<Void> update(Integer id, Category category) {
        // Workaround for patch
        HttpClient client = HttpClients.createDefault();
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

        HttpEntity<Category> entity = new HttpEntity<>(category, headers);
        return restTemplate.exchange(
                createURLWithPort("/categories/" + id),
                HttpMethod.PATCH, entity, Void.class);
    }

    private ResponseEntity<Void> delete(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/categories/" + id),
                HttpMethod.DELETE, null, Void.class);
    }

    @Test
    @Order(1)
    public void saveCategory() {
        ResponseEntity<Category> fantasyResponse = save(fantasyCategory);
        ResponseEntity<Category> romanceResponse = save(romanceCategory);
        fantasyCategory = fantasyResponse.getBody();
        romanceCategory = romanceResponse.getBody();

        Assert.assertTrue(fantasyResponse.getStatusCode().equals(HttpStatus.CREATED) && romanceResponse.getStatusCode().equals(HttpStatus.CREATED));
        System.out.println("saveCategory passed");
    }

    @Test
    @Order(2)
    public void saveCategoryWithNulls() {
        ResponseEntity<Category> withoutNameResponse = save(categoryWithoutName);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, withoutNameResponse.getStatusCode());
        System.out.println("saveCategoryWithNulls passed");
    }

    @Test
    @Order(3)
    public void findAllCategories() {
        ResponseEntity<List> allCategoriesResponse = findAll();
        List<Object> list = allCategoriesResponse.getBody();

        List<Category> categories = list.stream()
                .map(category -> new ObjectMapper().convertValue(category, Category.class))
                .collect(Collectors.toList());

        Assert.assertEquals(categories.size(), 2);

        Long categoryCount = categories.stream()
                .filter(category -> category.getName().equals(fantasyCategory.getName()))
                .count();
        Long romanceCount = categories.stream()
                .filter(category -> category.getName().equals(romanceCategory.getName()))
                .count();
        Assert.assertTrue(categoryCount.equals(1L) && romanceCount.equals(1L));

        Assert.assertEquals(HttpStatus.OK, allCategoriesResponse.getStatusCode());
        System.out.println("findAllCategories passed");
    }

    @Test
    @Order(4)
    public void findCategoryById() {
        Integer fantasyId = fantasyCategory.getId();
        Integer romanceId = romanceCategory.getId();
        ResponseEntity<Category> fantasyResponse = findById(fantasyId);
        ResponseEntity<Category> romanceResponse = findById(romanceId);

        Assert.assertEquals(fantasyCategory, fantasyResponse.getBody());
        Assert.assertEquals(romanceCategory, romanceResponse.getBody());

        Assert.assertEquals(HttpStatus.OK, fantasyResponse.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, romanceResponse.getStatusCode());
        System.out.println("findCategoryById passed");
    }

    @Test
    @Order(5)
    public void findCategoryByIdWithWrongId() {
        ResponseEntity<Category> wrongIdResponse = findById(9001);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("findCategoryByIdWithWrongId passed");
    }

    @Test
    @Order(6)
    public void updateCategory() {
        Integer fantasyId = fantasyCategory.getId();
        fantasyCategory = new Category("Fantasy!", "Often inspired by real world myth and folklore");
        fantasyCategory.setId(fantasyId);
        ResponseEntity<Void> fantasyUpdateResponse = update(fantasyId, fantasyCategory);

        Assert.assertEquals(HttpStatus.ACCEPTED, fantasyUpdateResponse.getStatusCode());

        ResponseEntity<Category> fantasyResponse = findById(fantasyId);

        Assert.assertEquals(fantasyCategory, fantasyResponse.getBody());

        Assert.assertEquals(HttpStatus.OK, fantasyResponse.getStatusCode());
        System.out.println("updateCategory passed");
    }

    @Test
    @Order(7)
    public void updateCategoryWithWrongId() {
        ResponseEntity<Void> wrongIdResponse = update(9001, fantasyCategory);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("updateCategoryWithWrongId passed");
    }

    @Test
    @Order(8)
    public void deleteCategory() {
        Integer fantasyId = fantasyCategory.getId();
        Integer romanceId = romanceCategory.getId();

        ResponseEntity<Void> fantasyResponse = delete(fantasyId);
        ResponseEntity<Void> romanceResponse = delete(romanceId);

        Assert.assertEquals(HttpStatus.NO_CONTENT, fantasyResponse.getStatusCode());
        Assert.assertEquals(HttpStatus.NO_CONTENT, romanceResponse.getStatusCode());

        ResponseEntity<List> allCategoriesResponse = findAll();

        Assert.assertEquals(0, allCategoriesResponse.getBody().size());
        Assert.assertEquals(HttpStatus.OK, allCategoriesResponse.getStatusCode());
        System.out.println("deleteCategory passed");
    }

    @Test
    @Order(9)
    public void deleteCategoryWithWrongId() {
        ResponseEntity<Void> wrongIdResponse = delete(9001);

        Assert.assertEquals(HttpStatus.NOT_FOUND, wrongIdResponse.getStatusCode());
        System.out.println("deleteCategoryWithWrongId passed");
    }
}
