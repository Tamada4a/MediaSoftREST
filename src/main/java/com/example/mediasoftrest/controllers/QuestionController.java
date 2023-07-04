package com.example.mediasoftrest.controllers;

import com.example.mediasoftrest.dtos.*;
import com.example.mediasoftrest.helpers.QuestionGetter;
import com.example.mediasoftrest.helpers.Translator;
import com.example.mediasoftrest.mysql.interfaces.CategoryRepository;
import com.example.mediasoftrest.mysql.interfaces.QuestionsRepository;
import com.example.mediasoftrest.mysql.tables.Questions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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


    @RequestMapping(value = "/random", method = RequestMethod.GET)
    public ResponseEntity<?> getRandomQuestion() throws IOException {

        int chooser = new Random().nextInt(2);

        if (chooser == 0)
            return questionGetter.getQuestionFromDB();
        else {
            return questionGetter.getQuestionFromAPI();
        }
    }


    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseEntity<?> checkQuestionAnswer(InputAnswerDTO checkAnswerDTO){
        int id = checkAnswerDTO.getQuestion_id();

        ArrayList<Questions> questions = questionsRepository.findById(id);

        ResponseDTO response;

        if (questions.isEmpty()) {
            response = new ResponseDTO(HttpStatus.NOT_FOUND, "Вопрос не существует", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Questions question = questions.get(0);

        String correct_answer = question.getAnswer();
        boolean is_correct = correct_answer.equals(checkAnswerDTO.getAnswer());

        OutputAnswerDTO outputAnswerDTO = new OutputAnswerDTO(id, is_correct, correct_answer);

        response = new ResponseDTO(HttpStatus.OK, "", outputAnswerDTO);
        return ResponseEntity.ok(response);
    }
}
