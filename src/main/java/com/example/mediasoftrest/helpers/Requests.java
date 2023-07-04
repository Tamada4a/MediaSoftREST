package com.example.mediasoftrest.helpers;

import com.example.mediasoftrest.dtos.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class Requests {
    public static ResponseEntity<?> ok(Object result){
        ResponseDTO response = new ResponseDTO(HttpStatus.OK, "", result);
        return ResponseEntity.ok(response);
    }


    public static ResponseEntity<?> badRequest(HttpStatus code, String description, Object result){
        ResponseDTO response = new ResponseDTO(code, description, result);
        return ResponseEntity.status(code.value()).body(response);
    }
}
