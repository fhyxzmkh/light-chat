package com.light.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.dto.request.RegisterRequest;
import com.light.chat.domain.entity.UserInfo;
import com.light.chat.mapper.UserInfoMapper;
import com.light.chat.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.light.chat.domain.constants.constants.EMAIL_CODE_PREFIX;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getNickname();
        String emailCode = request.getEmailCode();

        if (email == null || password == null || nickname == null || emailCode == null) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        QueryWrapper<UserInfo> queryWrapper;
        UserInfo existingUser;

        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        existingUser = this.getOne(queryWrapper);
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickname", nickname);
        existingUser = this.getOne(queryWrapper);
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Nickname already taken");
        }

        String expectCode = stringRedisTemplate.opsForValue().get(EMAIL_CODE_PREFIX);


        return null;
    }

}