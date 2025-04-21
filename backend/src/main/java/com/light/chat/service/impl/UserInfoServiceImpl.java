package com.light.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.dto.request.RegisterRequest;
import com.light.chat.domain.po.UserInfo;
import com.light.chat.mapper.UserInfoMapper;
import com.light.chat.service.EmailCodeService;
import com.light.chat.service.UserInfoService;
import com.light.chat.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getNickname();
        String emailCode = request.getEmailCode();

        // 参数校验
        if (email == null || password == null || nickname == null || emailCode == null) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        // 检查邮箱是否已注册
        if (lambdaQuery().eq(UserInfo::getEmail, email).one() != null) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // 检查昵称是否已存在
        if (lambdaQuery().eq(UserInfo::getNickname, nickname).one() != null) {
            return ResponseEntity.badRequest().body("Nickname already taken");
        }

        ResponseEntity<?> codeValidation = emailCodeService.isValidEmailCode(email, emailCode);
        if (!codeValidation.getStatusCode().is2xxSuccessful()) {
            return codeValidation;
        }

        String userUuid = CaptchaUtil.generateNumberCode(20);

        UserInfo newUser = UserInfo.builder()
                .uuid(userUuid)
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        userInfoMapper.insert(newUser);

        return ResponseEntity.ok("注册成功");
    }

}