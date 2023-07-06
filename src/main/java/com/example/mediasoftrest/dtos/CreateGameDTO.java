package com.example.mediasoftrest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;


@Data
@AllArgsConstructor
public class CreateGameDTO {
    private Integer qCount;

    private Integer minDifficulty;

    private Integer maxDifficulty;

    private ArrayList<String> categories;
}
