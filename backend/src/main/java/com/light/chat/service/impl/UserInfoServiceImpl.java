package com.light.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.config.security.UserDetailsImpl;
import com.light.chat.domain.dto.account.RegisterOrLoginRequest;
import com.light.chat.domain.dto.account.UserInfoDto;
import com.light.chat.domain.enums.ResponseCodeEnum;
import com.light.chat.domain.po.UserInfo;
import com.light.chat.exception.BusinessException;
import com.light.chat.mapper.UserInfoMapper;
import com.light.chat.service.EmailCodeService;
import com.light.chat.service.UserInfoService;
import com.light.chat.utils.CaptchaUtil;
import com.light.chat.utils.JwtUtil;
import com.light.chat.utils.ObjectUtil;
import com.light.chat.utils.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterOrLoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getNickname();
        String emailCode = request.getEmailCode();

        // 参数校验
        if (email == null || password == null || nickname == null || emailCode == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 检查邮箱是否已注册
        if (lambdaQuery().eq(UserInfo::getEmail, email).one() != null) {
            throw new BusinessException("Email already registered");
        }

        // 检查昵称是否已存在
        if (lambdaQuery().eq(UserInfo::getNickname, nickname).one() != null) {
            throw new BusinessException("Nickname already taken");
        }

        emailCodeService.isValidEmailCode(email, emailCode);

        String userUuid = CaptchaUtil.generateNumberCode(20);

        UserInfo newUser = UserInfo.builder()
                .uuid(userUuid)
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        userInfoMapper.insert(newUser);

        return "注册成功";
    }

    @Override
    public JSONObject login(RegisterOrLoginRequest request) {
        String username = request.getNickname();
        String password = request.getPassword();

        if (username == null || password == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();

        UserInfo user = loginUser.getUser();

        JSONObject result = new JSONObject();
        result.put("token", JwtUtil.createJWT(user.getUuid()));

        return result;
    }

    @Override
    public String updateUserInfo(UserInfo userInfo) {
        UserInfo user = SecurityUtil.getLoginUser();

        BeanUtils.copyProperties(userInfo, user, ObjectUtil.getNullPropertyNames(userInfo));

        userInfoMapper.update(user, new QueryWrapper<UserInfo>().eq("uuid", user.getUuid()));

        return "更新用户信息成功";
    }

    @Override
    public JSONObject getUserInfo() {
        UserInfo user = SecurityUtil.getLoginUser();
        UserInfoDto userInfoDto = new UserInfoDto();
        BeanUtils.copyProperties(user, userInfoDto);

        JSONObject result = new JSONObject();
        result.put("userInfo", userInfoDto);

        return result;
    }

}