package com.example.mediasoftrest.controllers;

import com.example.mediasoftrest.dtos.*;
import com.example.mediasoftrest.helpers.QuestionChecker;
import com.example.mediasoftrest.helpers.QuestionGetter;
import com.example.mediasoftrest.helpers.Requests;
import com.example.mediasoftrest.mysql.interfaces.QuestionsRepository;
import com.example.mediasoftrest.mysql.tables.Questions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private QuestionGetter questionGetter;

    @Autowired
    private QuestionChecker questionChecker;


    @RequestMapping(value = "/random", method = RequestMethod.GET)
    public ResponseEntity<?> getRandomQuestion() throws IOException {

        int chooser = new Random().nextInt(2);

        if (chooser == 0) {
            return questionGetter.getQuestionFromDB();
        }
        else {
            return questionGetter.getQuestionFromAPI();
        }
    }


    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseEntity<?> checkQuestionAnswer(InputAnswerDTO checkAnswerDTO){
        if (checkAnswerDTO.getQuestion_id() == null) {
            return Requests.badRequest(HttpStatus.NO_CONTENT, "Не указан id вопроса", null);
        }

        if (checkAnswerDTO.getAnswer() == null) {
            return Requests.badRequest(HttpStatus.NO_CONTENT, "Не указан ответ на вопрос", null);
        }

        return questionChecker.checkAnswer(checkAnswerDTO);
    }
}
