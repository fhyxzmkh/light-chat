package com.light.chat.domain.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {

    private String uuid;

    private String nickname;

    private String email;

    private String avatar;

    private Integer gender;

    private String signature;

    private String birthday;

    private Integer isAdmin;

    private Integer status;

    private Date createdAt;

}
