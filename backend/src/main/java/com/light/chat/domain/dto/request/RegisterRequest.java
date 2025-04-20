package com.light.chat.domain.dto.request;

import com.light.chat.domain.entity.UserInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest extends UserInfo {

    private String emailCode;

}
