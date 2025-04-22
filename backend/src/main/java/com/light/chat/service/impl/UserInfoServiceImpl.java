package com.light.chat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.config.security.UserDetailsImpl;
import com.light.chat.domain.dto.request.RegisterOrLoginRequest;
import com.light.chat.domain.dto.response.UserInfoDto;
import com.light.chat.domain.po.UserInfo;
import com.light.chat.mapper.UserInfoMapper;
import com.light.chat.service.EmailCodeService;
import com.light.chat.service.UserInfoService;
import com.light.chat.utils.CaptchaUtil;
import com.light.chat.utils.JwtUtil;
import com.light.chat.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> register(RegisterOrLoginRequest request) {
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

    @Override
    public ResponseEntity<?> login(RegisterOrLoginRequest request) {
        String username = request.getNickname();
        String password = request.getPassword();

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();

        UserInfo user = loginUser.getUser();

        JSONObject result = new JSONObject();
        result.put("token", JwtUtil.createJWT(user.getUuid()));

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> updateUserInfo(UserInfo userInfo) {
        UserInfo user = SecurityUtil.getLoginUser();

        BeanUtils.copyProperties(userInfo, user, getNullPropertyNames(userInfo));

        userInfoMapper.update(user, new QueryWrapper<UserInfo>().eq("uuid", user.getUuid()));

        return ResponseEntity.ok("更新用户信息成功");
    }

    @Override
    public ResponseEntity<?> getUserInfo() {
        UserInfo user = SecurityUtil.getLoginUser();
        UserInfoDto userInfoDto = new UserInfoDto();
        BeanUtils.copyProperties(user, userInfoDto);

        JSONObject result = new JSONObject();
        result.put("userInfo", userInfoDto);

        return ResponseEntity.ok(result);
    }

    // 获取对象中为null的属性名
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        return emptyNames.toArray(new String[0]);
    }

}