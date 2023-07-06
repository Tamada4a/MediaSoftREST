package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class NewGameDTO {
    private String id;

    private int qCount;
}
