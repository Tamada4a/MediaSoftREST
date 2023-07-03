package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;


@Data
@AllArgsConstructor
public class QuestionDTO {

    private int id;

    private String question;

    private HashMap<String, Object> category;

    private int difficulty;
}
