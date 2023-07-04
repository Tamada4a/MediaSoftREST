package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class InputAnswerDTO {

    private Object question_id;

    private String answer;
}
