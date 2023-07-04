package com.example.mediasoftrest.helpers;

import com.example.mediasoftrest.dtos.InputAnswerDTO;
import com.example.mediasoftrest.dtos.OutputAnswerDTO;
import com.example.mediasoftrest.mysql.interfaces.QuestionsRepository;
import com.example.mediasoftrest.mysql.tables.Questions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


@Service
public class QuestionChecker {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private QuestionsRepository questionsRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private String correctAnswer;


    public ResponseEntity<?> checkAnswer(InputAnswerDTO inputAnswerDTO){
        Object id = inputAnswerDTO.getQuestion_id();
        String answer = inputAnswerDTO.getAnswer();

        try {
            int idInt = Integer.parseInt(id.toString());
            return getDBAnswer(idInt, answer);
        } catch (NumberFormatException e) {
            return getAPIAnswer(id, answer);
        }
    }


    private ResponseEntity<?> getDBAnswer(int id, String answer){
        ArrayList<Questions> questions = questionsRepository.findById(id);

        if (questions.isEmpty()){
            return Requests.badRequest(HttpStatus.NO_CONTENT, "Вопроса с id " + id + " не существует", null);
        }

        correctAnswer = questions.get(0).getAnswer();

        boolean is_correct = correctAnswer.equals(answer);
        OutputAnswerDTO outputAnswerDTO = new OutputAnswerDTO(id, is_correct, correctAnswer);

        return Requests.ok(outputAnswerDTO);
    }


    private ResponseEntity<?> getAPIAnswer(Object id, String answer){
        Cache cache = getCache();
        ValueWrapper valueWrapper = cache.get(id);

        if (valueWrapper == null){
            String urlAPI = "https://the-trivia-api.com/v2/question/" + id;

            try {
                HashMap<String, Object> apiQuestion = restTemplate.getForObject(urlAPI, HashMap.class);

                String answerEng = (String) apiQuestion.get("correctAnswer");
                correctAnswer = Translator.translate("en", "ru", answerEng);

                cache.put(id, correctAnswer);
            } catch (HttpClientErrorException.NotFound e) {
                return Requests.badRequest(HttpStatus.NO_CONTENT, "Вопроса с id " + id + " не существует", null);
            } catch (IOException e) {
                return Requests.badRequest(HttpStatus.REQUEST_TIMEOUT, "Не удалось перевести текст", null);
            }
        } else {
            correctAnswer = (String) valueWrapper.get();
        }

        boolean is_correct = correctAnswer.equals(answer);
        OutputAnswerDTO outputAnswerDTO = new OutputAnswerDTO(id, is_correct, correctAnswer);
        return Requests.ok(outputAnswerDTO);
    }


    private Cache getCache(){
        return cacheManager.getCache("questions");
    }
}
