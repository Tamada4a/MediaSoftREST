package com.example.mediasoftrest.helpers;

import com.example.mediasoftrest.dtos.QuestionDTO;
import com.example.mediasoftrest.dtos.ResponseDTO;
import com.example.mediasoftrest.mysql.interfaces.CategoryRepository;
import com.example.mediasoftrest.mysql.interfaces.QuestionsRepository;
import com.example.mediasoftrest.mysql.tables.Questions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


@Service
public class QuestionGetter {

    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private ResponseDTO response;


    public ResponseEntity<?> getQuestionFromAPI() throws IOException {
        String urlAPI = "https://the-trivia-api.com/v2/questions?limit=1";
        HashMap<String, Object> apiQuestion = (HashMap<String, Object>) restTemplate.getForObject(urlAPI, ArrayList.class).get(0);

        response = new ResponseDTO(HttpStatus.OK, "", APIQuestionToDTO(apiQuestion));
        return ResponseEntity.ok(response);
    }


    public ResponseEntity<?> getQuestionFromDB(){
        List<Questions> questions = questionsRepository.findAll();

        if (questions.isEmpty()) {
            response = new ResponseDTO(HttpStatus.NO_CONTENT, "Список вопросов пуст", null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        int randomIdx = new Random().nextInt(questions.size());

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
