package com.light.chat.domain.dto.request;

import com.light.chat.domain.po.UserInfo;
import lombok.Getter;
import lombok.Setter;


public class RegisterOrLoginRequest extends UserInfo {

    @Getter
    @Setter
    private String emailCode;

    @Getter
    @Setter
    private String checkCode;

}
