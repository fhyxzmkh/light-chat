package com.light.chat.service;

import com.light.chat.domain.dto.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface UserInfoService {

    ResponseEntity<?> register(RegisterRequest request);

}