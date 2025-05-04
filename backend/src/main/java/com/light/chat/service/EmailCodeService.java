package com.light.chat.service;

import org.springframework.http.ResponseEntity;

public interface EmailCodeService {

    String sendEmailCode(String email, Integer type);

    void isValidEmailCode(String email, String code);
}