package com.light.chat.controller.account;

import com.light.chat.domain.dto.account.RegisterOrLoginRequest;
import com.light.chat.domain.po.UserInfo;
import com.light.chat.service.UserInfoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.light.chat.domain.constants.constants.CHECK_CODE_KEY;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterOrLoginRequest registerRequest, HttpSession session) {
        String checkCode = registerRequest.getCheckCode();
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(CHECK_CODE_KEY))) {
                return ResponseEntity.badRequest().body("验证码错误");
            }
            return userInfoService.register(registerRequest);
        } finally {
            session.removeAttribute(CHECK_CODE_KEY);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpSession session, @RequestBody RegisterOrLoginRequest loginRequest) {
        String checkCode = loginRequest.getCheckCode();
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(CHECK_CODE_KEY))) {
                return ResponseEntity.badRequest().body("验证码错误");
            }
            return userInfoService.login(loginRequest);
        } finally {
            session.removeAttribute(CHECK_CODE_KEY);
        }
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserInfo userInfo) {
        return userInfoService.updateUserInfo(userInfo);
    }

    @GetMapping("/profile/get")
    public ResponseEntity<?> getUserInfo() {
        return userInfoService.getUserInfo();
    }

}
