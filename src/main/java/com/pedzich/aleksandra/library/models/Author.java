package com.pedzich.aleksandra.library.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "author")
@NoArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private  Integer id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="surname", nullable=false)
    private String surname;

    @Column(name="bio")
    private String bio;

    @OneToMany(mappedBy = "author",
                cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Book> books;

    public Author(String name, String surname, String bio) {
        this.name = name;
        this.surname = surname;
        this.bio = bio;
    }
}
