package com.pedzich.aleksandra.library.repositories;

import com.pedzich.aleksandra.library.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

}