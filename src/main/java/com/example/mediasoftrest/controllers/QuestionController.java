package com.example.mediasoftrest.controllers;

import com.example.mediasoftrest.dtos.InputAnswerDTO;
import com.example.mediasoftrest.dtos.OutputAnswerDTO;
import com.example.mediasoftrest.dtos.QuestionDTO;
import com.example.mediasoftrest.dtos.ResponseDTO;
import com.example.mediasoftrest.mysql.interfaces.CategoryRepository;
import com.example.mediasoftrest.mysql.interfaces.QuestionsRepository;
import com.example.mediasoftrest.mysql.tables.Category;
import com.example.mediasoftrest.mysql.tables.Questions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


@RestController
public class QuestionController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    QuestionsRepository questionsRepository;


    @RequestMapping(value = "/question/random", method = RequestMethod.GET)
    public ResponseEntity<?> getRandomQuestion(){
        List<Questions> questions = questionsRepository.findAll();
        ResponseDTO response;

        if (questions.isEmpty()) {
            response = new ResponseDTO(HttpStatus.NO_CONTENT, "Questions list is empty", null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        Random random = new Random();
        int randomIdx = random.nextInt(questions.size());

        QuestionDTO randomQuestion = questionToDTO(questions.get(randomIdx));

        if (randomQuestion == null) {
            response = new ResponseDTO(HttpStatus.NOT_FOUND, "Category didn't exist", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response = new ResponseDTO(HttpStatus.OK, "", randomQuestion);
        return ResponseEntity.ok(response);
    }


    @RequestMapping(value = "/question/check", method = RequestMethod.POST)
    public ResponseEntity<?> checkQuestionAnswer(InputAnswerDTO checkAnswerDTO){
        int id = checkAnswerDTO.getQuestion_id();

        ArrayList<Questions> questions = questionsRepository.findById(id);

        ResponseDTO response;

        if (questions.isEmpty()) {
            response = new ResponseDTO(HttpStatus.NOT_FOUND, "Question didn't exist", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Questions question = questions.get(0);

        String correct_answer = question.getAnswer();
        boolean is_correct = correct_answer.equals(checkAnswerDTO.getAnswer());

        OutputAnswerDTO outputAnswerDTO = new OutputAnswerDTO(id, is_correct, correct_answer);

        response = new ResponseDTO(HttpStatus.OK, "", outputAnswerDTO);
        return ResponseEntity.ok(response);
    }


    private QuestionDTO questionToDTO(Questions question){
        int catId = question.getId();
        ArrayList<Category> categories = categoryRepository.findById(catId);

        if (categories.isEmpty())
            return null;

        String catName = categories.get(0).getName();

        HashMap<String, Object> new_category = new HashMap<>();
        new_category.put("id", catId);
        new_category.put("name", catName);

        return new QuestionDTO(catId, question.getQuestion(), new_category, question.getDifficulty());
    }
}
