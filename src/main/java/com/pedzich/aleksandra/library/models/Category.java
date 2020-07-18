package com.pedzich.aleksandra.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private  Integer id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="description")
    private String description;

    @ManyToMany
    @JoinTable(name="book_category",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="book_id"))
    @JsonIgnore
    List<Book> books;

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
