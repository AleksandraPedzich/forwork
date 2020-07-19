package com.pedzich.aleksandra.library;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedzich.aleksandra.library.models.Category;
import org.junit.Assert;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

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

    private ResponseEntity<Void> save(Category category) {
        HttpEntity<Category> entity = new HttpEntity<>(category, headers);
        return restTemplate.exchange(
                createURLWithPort("/categories"),
                HttpMethod.POST, entity, Void.class);
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
        HttpEntity<Category> entity = new HttpEntity<>(category, headers);
        return restTemplate.exchange(
                createURLWithPort("/categories/" + id + "?_method=patch"),
                HttpMethod.POST, entity, Void.class);
    }

    private ResponseEntity<Void> delete(Integer id) {
        return restTemplate.exchange(
                createURLWithPort("/categories/" + id),
                HttpMethod.DELETE, null, Void.class);
    }

    @Test
    @Order(1)
    public void saveCategory() {
        ResponseEntity<Void> fantasyResponse = save(fantasyCategory);
        ResponseEntity<Void> romanceResponse = save(romanceCategory);

        System.out.println(fantasyCategory);

        Assert.assertTrue(fantasyResponse.getStatusCode().equals(HttpStatus.CREATED) && romanceResponse.getStatusCode().equals(HttpStatus.CREATED));
        System.out.println("saveCategory passed");
    }

    @Test
    @Order(2)
    public void saveCategoryWithNulls() {
        ResponseEntity<Void> withoutNameResponse = save(categoryWithoutName);

        Assert.assertEquals(withoutNameResponse.getStatusCode(), HttpStatus.BAD_REQUEST);
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

        for (Category category: categories) {
            if (category.getName().equals("Fantasy"))
                fantasyCategory = category;
            else
                romanceCategory = category;
        }

        Assert.assertEquals(allCategoriesResponse.getStatusCode(), HttpStatus.OK);
        System.out.println("findAllCategories passed");
    }

    @Test
    @Order(4)
    public void findCategoryById() {
        Integer fantasyId = fantasyCategory.getId();
        Integer romanceId = romanceCategory.getId();
        ResponseEntity<Category> fantasyResponse = findById(fantasyId);
        ResponseEntity<Category> romanceResponse = findById(romanceId);

        Assert.assertEquals(fantasyResponse.getBody(), fantasyCategory);
        Assert.assertEquals(romanceResponse.getBody(), romanceCategory);

        Assert.assertEquals(fantasyResponse.getStatusCode(), HttpStatus.OK);
        Assert.assertEquals(romanceResponse.getStatusCode(), HttpStatus.OK);
        System.out.println("findCategoryById passed");
    }

    @Test
    @Order(5)
    public void findCategoryByIdWithWrongId() {
        ResponseEntity<Category> wrongIdResponse = findById(9001);

        Assert.assertEquals(wrongIdResponse.getStatusCode(), HttpStatus.NOT_FOUND);
        System.out.println("findCategoryByIdWithWrongId passed");
    }

    /*@Test
    @Order(6)
    public void updateCategory() {
        Integer fantasyId = fantasyCategory.getId();
        fantasyCategory = new Category("Fantasy!", "Often inspired by real world myth and folklore");
        ResponseEntity<Void> fantasyUpdateResponse = update(fantasyId, fantasyCategory);

        Assert.assertEquals(fantasyUpdateResponse.getStatusCode(), HttpStatus.ACCEPTED);

        ResponseEntity<Category> fantasyResponse = findById(fantasyId);

        Assert.assertEquals(fantasyResponse.getBody(), fantasyCategory);

        Assert.assertEquals(fantasyResponse.getStatusCode(), HttpStatus.OK);
        System.out.println("saveCategory passed");
    }*/

    @Test
    @Order(6)
    public void deleteCategory() {
        Integer fantasyId = fantasyCategory.getId();
        Integer romanceId = romanceCategory.getId();

        ResponseEntity<Void> fantasyResponse = delete(fantasyId);
        ResponseEntity<Void> romanceResponse = delete(romanceId);

        Assert.assertEquals(fantasyResponse.getStatusCode(), HttpStatus.NO_CONTENT);
        Assert.assertEquals(romanceResponse.getStatusCode(), HttpStatus.NO_CONTENT);

        ResponseEntity<List> allCategoriesResponse = findAll();

        Assert.assertEquals(allCategoriesResponse.getBody().size(), 0);
        Assert.assertEquals(allCategoriesResponse.getStatusCode(), HttpStatus.OK);
        System.out.println("saveCategory passed");
    }
}
