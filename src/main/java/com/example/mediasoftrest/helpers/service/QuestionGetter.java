package com.example.mediasoftrest.helpers.service;

import com.example.mediasoftrest.dtos.QuestionDTO;
import com.example.mediasoftrest.helpers.Requests;
import com.example.mediasoftrest.helpers.Translator;
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


    public ResponseEntity<?> getQuestion() throws IOException {
        int chooser = new Random().nextInt(2);

        if (chooser == 0) {
            return getQuestionFromDB();
        }
        else {
            return getQuestionFromAPI();
        }
    }


    private ResponseEntity<?> getQuestionFromAPI() throws IOException {
        String urlAPI = "https://the-trivia-api.com/v2/questions?limit=1";
        HashMap<String, Object> apiQuestion = (HashMap<String, Object>) restTemplate.getForObject(urlAPI, ArrayList.class).get(0);

        return Requests.ok(APIQuestionToDTO(apiQuestion));
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


    private ResponseEntity<?> getQuestionFromDB(){
        List<Questions> questions = questionsRepository.findAll();

        if (questions.isEmpty()) {
            return Requests.badRequest(HttpStatus.NOT_FOUND, "Список вопросов пуст", null);
        }

        int randomIdx = new Random().nextInt(questions.size());

        QuestionDTO randomQuestion = DBQuestionToDTO(questions.get(randomIdx));

        return Requests.ok(randomQuestion);
    }


    private QuestionDTO DBQuestionToDTO(Questions question){
        int catId = question.getCategory();
        String catName = categoryRepository.findById(catId).get(0).getName();

        HashMap<String, Object> new_category = new HashMap<>();
        new_category.put("id", catId);
        new_category.put("name", catName);

        return new QuestionDTO(question.getId(), question.getQuestion(), new_category, question.getDifficulty());
    }
}
