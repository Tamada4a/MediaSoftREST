package com.example.mediasoftrest.helpers.service;

import com.example.mediasoftrest.dtos.CreateGameDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateGameTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void createGame1() {
        String url = "http://localhost:" + port + "/game/";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new CreateGameDTO(10, 0, 800, null)), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        Object result = response.get("result");
        assert result != null;
    }


    @Test
    void createGame2() {
        String url = "http://localhost:" + port + "/game/";
        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new CreateGameDTO()), HashMap.class);

        assert response != null;

        assert response.get("code").equals("BAD_REQUEST");

        assert "Не указано количество игр".equals(response.get("description"));

        Object result = response.get("result");
        assert result == null;
    }


    @Test
    void createGame3() {
        String url = "http://localhost:" + port + "/game/";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new CreateGameDTO(10, null, null, null)), HashMap.class);

        assert response != null;

        assert response.get("code").equals("BAD_REQUEST");

        assert "Не указана минимальная сложность".equals(response.get("description"));

        Object result = response.get("result");
        assert result == null;
    }


    @Test
    void createGame4() {
        String url = "http://localhost:" + port + "/game/";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new CreateGameDTO(10, 0, null, null)), HashMap.class);

        assert response != null;

        assert response.get("code").equals("BAD_REQUEST");

        assert "Не указана максимальная сложность".equals(response.get("description"));

        Object result = response.get("result");
        assert result == null;
    }


    @Test
    void createGame5() {
        String url = "http://localhost:" + port + "/game/";

        HashMap<String, Object> response = restTemplate.postForObject(url, new HttpEntity<>(new CreateGameDTO(10, 0, 800, new ArrayList<>(List.of("Фильмы")))), HashMap.class);

        assert response != null;

        assert response.get("code").equals("OK");

        Object result = response.get("result");
        assert result != null;
    }
}
