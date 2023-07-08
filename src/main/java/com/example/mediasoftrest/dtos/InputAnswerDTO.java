package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputAnswerDTO {

    private Object question_id;

    private String answer;
}
