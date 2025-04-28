package com.light.chat.service;

import com.light.chat.domain.dto.account.RegisterOrLoginRequest;
import com.light.chat.domain.po.UserInfo;
import org.springframework.http.ResponseEntity;

public interface UserInfoService {

    ResponseEntity<?> register(RegisterOrLoginRequest request);

    ResponseEntity<?> login(RegisterOrLoginRequest loginRequest);

    ResponseEntity<?> updateUserInfo(UserInfo userInfo);

    ResponseEntity<?> getUserInfo();

}