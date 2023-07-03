package com.example.mediasoftrest.mysql.tables;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "category")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_cat", nullable = false, unique = true)
    private int id;

    @Column(name="name_cat", nullable = false)
    private String name;
}
