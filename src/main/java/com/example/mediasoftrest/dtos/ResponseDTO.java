package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
@AllArgsConstructor
public class ResponseDTO {

    private HttpStatus code; //статус выполнения запроса

    private String description; //описание ошибки

    private Object result; //тело результата выполнения запроса
}
