package com.example.mediasoftrest.controllers;

import com.example.mediasoftrest.dtos.*;
import com.example.mediasoftrest.helpers.service.QuestionChecker;
import com.example.mediasoftrest.helpers.service.QuestionGetter;
import com.example.mediasoftrest.helpers.Requests;
import com.example.mediasoftrest.mysql.interfaces.QuestionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


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
        return questionGetter.getQuestion();
    }


    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseEntity<?> checkQuestionAnswer(@RequestBody InputAnswerDTO checkAnswerDTO){
        if (checkAnswerDTO.getQuestion_id() == null) {
            return Requests.badRequest(HttpStatus.NOT_FOUND, "Не указан id вопроса", null);
        }

        if (checkAnswerDTO.getAnswer() == null) {
            return Requests.badRequest(HttpStatus.NOT_FOUND, "Не указан ответ на вопрос", null);
        }

        return questionChecker.checkAnswer(checkAnswerDTO);
    }
}
