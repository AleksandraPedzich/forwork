package com.pedzich.aleksandra.library;

import com.pedzich.aleksandra.library.models.Category;
import org.junit.Assert;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryTests {

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Value("${application.address}")
    private String applicationAddress;

    @Value("${application.port}")
    private String applicationPort;

    private Category fantasyCategory = new Category("Fantasy", null);
    private Category romanceCategory = new Category("Romance", "Love stories");
    private Category categoryWithoutName = new Category(null, "Category without a name");

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

        List<Category> categories = (List<Category>) allCategoriesResponse.getBody();

        Assert.assertEquals(categories.size(), 2);

        System.out.println(categories);

        /*Long categoryCount = categories.stream()
                .filter(category -> category.getName().equals(fantasyCategory.getName()))
                .count();
        Long romanceCount = categories.stream()
                .filter(category -> category.getName().equals(romanceCategory.getName()))
                .count();
        Assert.assertTrue(categoryCount.equals(1) && romanceCount.equals(1));*/

        Assert.assertEquals(allCategoriesResponse.getStatusCode(), HttpStatus.OK);

        System.out.println("findAllCategories passed");
    }

}
