package com.example.mediasoftrest.mysql.interfaces;

import com.example.mediasoftrest.mysql.tables.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;


@Repository
public interface QuestionsRepository extends JpaRepository<Questions, String> {
    ArrayList<Questions> findById(final int id);
    ArrayList<Questions> findByQuestion(final String question);
    ArrayList<Questions> findByAnswer(final String answer);
    ArrayList<Questions> findByCategory(final int category);
}
