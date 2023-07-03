package com.example.mediasoftrest.mysql.interfaces;

import com.example.mediasoftrest.mysql.tables.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    ArrayList<Category> findById(final int id);
    ArrayList<Category> findByName(final String name);
}
