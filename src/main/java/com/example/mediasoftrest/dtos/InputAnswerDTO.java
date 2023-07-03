package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class InputAnswerDTO {

    private int question_id;

    private String answer;
}
