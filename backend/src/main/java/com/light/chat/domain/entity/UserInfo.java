package com.light.chat.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "user_info", autoResultMap = true)
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private String uuid;

    @TableField("nickname")
    private String nickname;

    @TableField("email")
    private String email;

    @TableField("avatar")
    private String avatar = "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png";

    @TableField("gender")
    private Integer gender;

    @TableField("signature")
    private String signature;

    @TableField("password")
    private String password;

    @TableField("birthday")
    private String birthday;

    @TableField("created_at")
    private Date createdAt;

    @TableField("deleted_at")
    private Date deletedAt;

    @TableField("is_admin")
    private Integer isAdmin;

    @TableField("status")
    private Integer status;
}