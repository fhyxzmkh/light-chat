package com.light.chat.service;

import com.alibaba.fastjson.JSONObject;
import com.light.chat.domain.dto.account.RegisterOrLoginRequest;
import com.light.chat.domain.po.UserInfo;

public interface UserInfoService {

    String register(RegisterOrLoginRequest request);

    JSONObject login(RegisterOrLoginRequest loginRequest);

    String updateUserInfo(UserInfo userInfo);

    JSONObject getUserInfo();

}