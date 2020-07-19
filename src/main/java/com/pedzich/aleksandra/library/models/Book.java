package com.pedzich.aleksandra.library.models;

import com.pedzich.aleksandra.library.enums.Type;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "book")
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private  Integer id;

    @Column(name="isbn", nullable=false)
    private String isbn;

    @Column(name="title", nullable=false)
    private String title;

    @Column(name="type", nullable=false)
    private Type type;

    /**
     * To use different kind of advanced mappings I assume book will have one author
     */
    @ManyToOne
    @JoinColumn(name="author_id", nullable=false)
    private Author author;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name="book_category",
        joinColumns = @JoinColumn(name="book_id"),
        inverseJoinColumns = @JoinColumn(name="category_id"))
    List<Category> categories;

    public Book(Integer id, String isbn, String title, Type type, Author author, List<Category> categories) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.type = type;
        this.author = author;
        this.categories = categories;
    }

    public Book(String isbn, String title, Type type, Author author, List<Category> categories) {
        this.isbn = isbn;
        this.title = title;
        this.type = type;
        this.author = author;
        this.categories = categories;
    }
}
