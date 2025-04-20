package com.light.chat.service;

import org.springframework.http.ResponseEntity;

public interface EmailCodeService {

    ResponseEntity<?> sendEmailCode(String email, Integer type);

}