package com.example.mediasoftrest.helpers.service;

import com.example.mediasoftrest.dtos.*;
import com.example.mediasoftrest.helpers.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service
public class GameService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private QuestionGetter questionGetter;

    @Autowired
    private QuestionChecker questionChecker;


    public ResponseEntity<?> createGame(CreateGameDTO createGameDTO) throws IOException {
        int size = createGameDTO.getQCount();

        ArrayList<QuestionDTO> questions = new ArrayList<>();
        String uniqueID = UUID.randomUUID().toString();

        for (int i = 0; i < size; ++i){
            ResponseDTO responseDTO = (ResponseDTO) questionGetter.getQuestion().getBody();
            QuestionDTO questionDTO = (QuestionDTO) responseDTO.getResult();

            boolean isAdd = true;

            int difficulty = questionDTO.getDifficulty();
            if (difficulty < createGameDTO.getMinDifficulty() && difficulty > createGameDTO.getMaxDifficulty()){
                isAdd = false;
            }

            String category = (String) questionDTO.getCategory().get("name");
            ArrayList<String> createCategories = createGameDTO.getCategories();
            if (createCategories != null && !createCategories.contains(category)){
                isAdd = false;
            }

            if (!isUnique(questionDTO.getId(), questions)){
                isAdd = false;
            }

            if (isAdd){
                questions.add(questionDTO);
            }
        }

        getGamesCache().put(uniqueID, questions);
        NewGameDTO newGame = new NewGameDTO(uniqueID, questions.size());

        return Requests.ok(newGame);
    }


    public ResponseEntity<?> getGameQuestion(String gameID, int qIdx){

        ResponseEntity<?> validation = validation(gameID, qIdx);

        if (validation.getStatusCode() != HttpStatus.OK){
            return validation;
        }

        return Requests.ok(getQuestion(gameID, qIdx));
    }


    public ResponseEntity<?> checkGameQuestion(String gameID, int qIdx, String answer){

        ResponseEntity<?> validation = validation(gameID, qIdx);

        if (validation.getStatusCode() != HttpStatus.OK){
            return validation;
        }

        Object id = getQuestion(gameID, qIdx).getId();
        ResponseEntity<?> result = questionChecker.checkAnswer(new InputAnswerDTO(id, answer));
        OutputAnswerDTO body = (OutputAnswerDTO) ((ResponseDTO) result.getBody()).getResult();

        Cache cache = getAnswersCache();
        ValueWrapper valueWrapper = cache.get(gameID);

        HashMap<Integer, List<Object>> stats = new HashMap<>();
        if (valueWrapper == null){
            stats.put(qIdx, List.of(body.is_correct(), body.getCorrect_answer()));
        } else {
            stats = (HashMap<Integer, List<Object>>) valueWrapper.get();
            stats.put(qIdx, List.of(body.is_correct(), body.getCorrect_answer()));
        }
        cache.put(gameID, stats);

        return result;
    }


    public ResponseEntity<?> finishGame(String gameID){
        ValueWrapper valueWrapper = getGamesCache().get(gameID);

        if (valueWrapper == null){
            return Requests.badRequest(HttpStatus.NOT_FOUND, "Игры с таким ID не существует", null);
        }

        ArrayList<QuestionDTO> questions = (ArrayList<QuestionDTO>) valueWrapper.get();
        ArrayList<OutputAnswerDTO> result = new ArrayList<>();

        ValueWrapper answersWrapper = getAnswersCache().get(gameID);

        if (answersWrapper == null){
            for (int i = 0; i < questions.size(); ++i){
                result.add(new OutputAnswerDTO(questions.get(i).getId(), false, "Вы не дошли до этого вопроса"));
            }
        } else {
            HashMap<Integer, List<Object>> stats = (HashMap<Integer, List<Object>>) answersWrapper.get();

            for (int i = 0; i < questions.size(); ++i) {
                if (!stats.containsKey(i)) {
                    stats.put(i, List.of(false, "Вы не дошли до этого вопроса"));
                }
                result.add(new OutputAnswerDTO(questions.get(i).getId(), (Boolean) stats.get(i).get(0), (String) stats.get(i).get(1)));
            }
        }

        return Requests.ok(result);
    }


    private boolean isUnique(Object qID, ArrayList<QuestionDTO> questions){
        for (QuestionDTO question : questions){
            if (question.getId().equals(qID)){
                return false;
            }
        }
        return true;
    }


    private ResponseEntity<?> validation(String gameID, int qIdx){
        ValueWrapper valueWrapper = getGamesCache().get(gameID);

        if (valueWrapper == null){
            return Requests.badRequest(HttpStatus.NOT_FOUND, "Игры с таким ID не существует", null);
        }

        ArrayList<QuestionDTO> questions = (ArrayList<QuestionDTO>) valueWrapper.get();

        if (questions == null || questions.isEmpty()){
            return Requests.badRequest(HttpStatus.NOT_FOUND, "Не удалось получить список вопросов игры с таким ID", null);
        }

        if (questions.size() - 1 < qIdx){
            return Requests.badRequest(HttpStatus.NOT_FOUND, "Вопроса с таким порядковым номером нет", null);
        }

        return Requests.ok(null);
    }


    private QuestionDTO getQuestion(String gameID, int qIdx){
        ArrayList<QuestionDTO> questions = (ArrayList<QuestionDTO>) getGamesCache().get(gameID).get();
        return questions.get(qIdx);
    }


    private Cache getGamesCache(){
        return cacheManager.getCache("games");
    }


    private Cache getAnswersCache(){
        return cacheManager.getCache("answers");
    }
}
