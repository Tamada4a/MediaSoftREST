package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class OutputAnswerDTO {

    private int question_id;

    private boolean is_correct;

    private String correct_answer;
}
