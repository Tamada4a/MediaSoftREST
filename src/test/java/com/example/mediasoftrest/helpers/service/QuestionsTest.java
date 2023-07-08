package com.example.mediasoftrest.helpers.service;

import com.example.mediasoftrest.dtos.CreateGameDTO;
import com.example.mediasoftrest.dtos.InputAnswerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;

import java.util.HashMap;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuestionsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void getRandomQuestion() {
        String url = "http://localhost:" + port + "/question/random";
        HashMap<String, Object> response = (HashMap<String, Object>) restTemplate.getForObject(url, HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        HashMap<String, Object> result = (HashMap<String, Object>) response.get("result");
        assert result != null;

        assert result.get("question") != null;
        assert result.get("category") != null;
        assert result.get("difficulty") != null;
    }


    @Test
    void checkCorrectAnswer() {
        String url = "http://localhost:" + port + "/question/check";
        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(1, "Квентин Тарантино")), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        HashMap<String, Object> result = (HashMap<String, Object>) response.get("result");
        assert result != null;

        assert (boolean) result.get("_correct");
        assert result.get("question_id").equals(1);
        assert "Квентин Тарантино".equals(result.get("correct_answer"));
    }

    @Test
    void checkIncorrectAnswer() {
        String url = "http://localhost:" + port + "/question/check";
        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(1, "Мартин Скорсезе")), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        HashMap<String, Object> result = (HashMap<String, Object>) response.get("result");
        assert result != null;

        assert !(boolean) result.get("_correct");
        assert result.get("question_id").equals(1);
        assert "Квентин Тарантино".equals(result.get("correct_answer"));
    }


    @Test
    void checkIncorrectRequest1() {
        String url = "http://localhost:" + port + "/question/check";
        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO()), HashMap.class);

        assert response != null;
        assert response.get("code").equals("NOT_FOUND");

        assert "Не указан id вопроса".equals(response.get("description"));

        Object result = response.get("result");
        assert result == null;
    }


    @Test
    void checkIncorrectRequest2() {
        String url = "http://localhost:" + port + "/question/check";
        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(1, null)), HashMap.class);

        assert response != null;
        assert response.get("code").equals("NOT_FOUND");

        assert "Не указан ответ на вопрос".equals(response.get("description"));

        Object result = response.get("result");
        assert result == null;
    }


    @Test
    void checkNonExistedID() {
        String url = "http://localhost:" + port + "/question/check";
        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new InputAnswerDTO(100, "Test")), HashMap.class);

        assert response != null;
        assert response.get("code").equals("NOT_FOUND");

        assert "Вопроса с id 100 не существует".equals(response.get("description"));

        Object result = response.get("result");
        assert result == null;
    }
}
