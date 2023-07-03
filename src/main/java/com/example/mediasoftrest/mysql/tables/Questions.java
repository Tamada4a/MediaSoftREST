package com.example.mediasoftrest.mysql.tables;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "questions")
@Data
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_question", nullable = false, unique = true)
    private int id;

    @Column(name="question", nullable = false, unique = true)
    private String question;

    @Column(name="difficulty", nullable = false)
    private int difficulty;

    @Column(name="answer", nullable = false)
    private String answer;

    @Column(name="category_id", nullable = false)
    private int category;
}
