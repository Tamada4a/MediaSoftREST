package com.example.mediasoftrest.helpers.service;

import com.example.mediasoftrest.dtos.CreateGameDTO;
import com.example.mediasoftrest.dtos.InputAnswerDTO;
import com.example.mediasoftrest.dtos.OutputAnswerDTO;
import com.example.mediasoftrest.dtos.QuestionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    private Object gameID;
    private ArrayList<QuestionDTO> questions;


    @BeforeEach
    void createGame() {
        String url = "http://localhost:" + port + "/game/";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new CreateGameDTO(10, 0, 800, null)), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        HashMap<String, Object> result = (HashMap<String, Object>) response.get("result");
        assert result != null;

        gameID = result.get("id");
        questions = cacheManager.getCache("games").get(gameID, ArrayList.class);
    }


    @Test
    void getFirstQuestion() {
        String url = "http://localhost:" + port + "/game/" + gameID + "/1";
        HashMap<String, Object> response = (HashMap<String, Object>) restTemplate.getForObject(url, HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        Object result = response.get("result");
        assert result != null;
    }


    @Test
    void answerFirstQuestion() {
        String url = "http://localhost:" + port + "/game/" + gameID + "/1/check";
        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(null, "Test")), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        HashMap<String, Object> result = (HashMap<String, Object>) response.get("result");
        assert result != null;

        assert !(boolean) result.get("_correct");
        assert !result.get("correct_answer").equals("Test");
    }

    @Test
    void answerRandomQuestion() {
        int qIdx = new Random().nextInt(questions.size());

        String url = "http://localhost:" + port + "/game/" + gameID + "/" + qIdx + "/check";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(null, "Test")), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        HashMap<String, Object> result = (HashMap<String, Object>) response.get("result");
        assert result != null;

        assert !(boolean) result.get("_correct");
        assert !result.get("correct_answer").equals("Test");
    }


    @Test
    void answerQuestionOutOfBound() {
        int qIdx = questions.size() + 3;

        String url = "http://localhost:" + port + "/game/" + gameID + "/" + qIdx + "/check";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(null, "Test")), HashMap.class);

        assert response != null;

        assert response.get("code").equals("NOT_FOUND");

        assert "Вопроса с таким порядковым номером нет".equals(response.get("description"));

        Object result = response.get("result");
        assert result == null;
    }


    @Test
    void finishGame() {
        String url = "http://localhost:" + port + "/game/" + gameID + "/finish";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(null, "Test")), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        ArrayList<OutputAnswerDTO> result = (ArrayList<OutputAnswerDTO>) response.get("result");
        assert result.size() == questions.size();
    }
}
