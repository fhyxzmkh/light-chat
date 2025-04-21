package com.light.chat.controller.account;

import com.light.chat.domain.dto.request.RegisterRequest;
import com.light.chat.service.UserInfoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.light.chat.domain.constants.constants.CHECK_CODE_KEY;

@RestController
public class AccountController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/account/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpSession session) {
        System.out.println("registerRequest = " + registerRequest.toString());
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

}
