package com.example.mediasoftrest.controllers;

import com.example.mediasoftrest.dtos.*;
import com.example.mediasoftrest.helpers.Translator;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    QuestionsRepository questionsRepository;

    private ResponseDTO response;

    private final Random random = new Random();

    private final RestTemplate restTemplate = new RestTemplate();


    @RequestMapping(value = "/random", method = RequestMethod.GET)
    public ResponseEntity<?> getRandomQuestion() throws IOException {
        int chooser = random.nextInt(2);

        if (chooser == 0)
            return getQuestionFromDB();
        else {
            return getQuestionFromAPI();
        }
    }


    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public ResponseEntity<?> checkQuestionAnswer(InputAnswerDTO checkAnswerDTO){
        int id = checkAnswerDTO.getQuestion_id();

        ArrayList<Questions> questions = questionsRepository.findById(id);

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


    private ResponseEntity<?> getQuestionFromAPI() throws IOException {
        String urlAPI = "https://the-trivia-api.com/v2/questions?limit=1";
        HashMap<String, Object> apiQuestion = (HashMap<String, Object>) restTemplate.getForObject(urlAPI, ArrayList.class).get(0);

        response = new ResponseDTO(HttpStatus.OK, "", APIQuestionToDTO(apiQuestion));
        return ResponseEntity.ok(response);
    }


    private ResponseEntity<?> getQuestionFromDB(){
        List<Questions> questions = questionsRepository.findAll();

        if (questions.isEmpty()) {
            response = new ResponseDTO(HttpStatus.NO_CONTENT, "Список вопросов пуст", null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        int randomIdx = random.nextInt(questions.size());

        QuestionDTO randomQuestion = DBQuestionToDTO(questions.get(randomIdx));

        response = new ResponseDTO(HttpStatus.OK, "", randomQuestion);
        return ResponseEntity.ok(response);
    }


    private QuestionDTO DBQuestionToDTO(Questions question){
        int catId = question.getCategory();
        String catName = categoryRepository.findById(catId).get(0).getName();

        HashMap<String, Object> new_category = new HashMap<>();
        new_category.put("id", catId);
        new_category.put("name", catName);

        return new QuestionDTO(question.getId(), question.getQuestion(), new_category, question.getDifficulty());
    }


    private QuestionDTO APIQuestionToDTO(HashMap<String, Object> apiQuestion) throws IOException {
        Object id = apiQuestion.get("id");

        HashMap<String, String> question = (HashMap<String, String>) apiQuestion.get("question");
        String questionText = Translator.translate("en", "ru", question.get("text"));

        String difficultyStr = (String) apiQuestion.get("difficulty");
        int difficulty = switch (difficultyStr) {
            case "medium" -> 450;
            case "hard" -> 800;
            default -> 200;
        };

        String categoryEn = (String) apiQuestion.get("category");
        String catName = Translator.translate("en", "ru", categoryEn);

        HashMap<String, Object> new_category = new HashMap<>();
        new_category.put("id", 0);
        new_category.put("name", catName);

        return new QuestionDTO(id, questionText, new_category, difficulty);
    }
}
