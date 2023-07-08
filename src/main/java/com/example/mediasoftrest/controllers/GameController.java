package com.example.mediasoftrest.controllers;

import com.example.mediasoftrest.dtos.CreateGameDTO;
import com.example.mediasoftrest.dtos.InputAnswerDTO;
import com.example.mediasoftrest.helpers.Requests;
import com.example.mediasoftrest.helpers.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createNewGame(@RequestBody CreateGameDTO createGameDTO) throws IOException {
        if (createGameDTO.getQCount() == null) {
            return Requests.badRequest(HttpStatus.BAD_REQUEST, "Не указано количество игр", null);
        }

        if (createGameDTO.getMinDifficulty() == null) {
            return Requests.badRequest(HttpStatus.BAD_REQUEST, "Не указана минимальная сложность", null);
        }

        if (createGameDTO.getMaxDifficulty() == null) {
            return Requests.badRequest(HttpStatus.BAD_REQUEST, "Не указана максимальная сложность", null);
        }

        return gameService.createGame(createGameDTO);
    }


    @RequestMapping(value = "/{game_id}/{question_number}", method = RequestMethod.GET)
    public ResponseEntity<?> getGameQuestion(@PathVariable("game_id") String game_id, @PathVariable("question_number") int question_number){
        return gameService.getGameQuestion(game_id, question_number);
    }


    @RequestMapping(value = "/{game_id}/{question_number}/check", method = RequestMethod.POST)
    public ResponseEntity<?> checkGameQuestion(@PathVariable("game_id") String game_id, @PathVariable("question_number") int question_number, String answer){
        return gameService.checkGameQuestion(game_id, question_number, answer);
    }


    @RequestMapping(value = "/{game_id}/finish", method = RequestMethod.POST)
    public ResponseEntity<?> finishGame(@PathVariable String game_id){
        return gameService.finishGame(game_id);
    }
}
