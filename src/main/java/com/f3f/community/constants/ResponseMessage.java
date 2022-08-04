package com.f3f.community.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseMessage {

    public static final ResponseEntity OK = ResponseEntity.ok().build();

    public static final ResponseEntity<String> CREATE = new ResponseEntity<>(
            "SUCCESS",HttpStatus.CREATED);
}
