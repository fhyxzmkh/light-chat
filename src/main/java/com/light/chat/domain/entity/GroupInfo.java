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
@TableName(value = "group_info", autoResultMap = true)
public class GroupInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private String uuid;

    @TableField("name")
    private String name;

    @TableField("notice")
    private String notice;

    @TableField(value = "members", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String members;

    @TableField("member_cnt")
    private Integer memberCnt = 1;

    @TableField("owner_id")
    private String ownerId;

    @TableField("add_mode")
    private Integer addMode = 0;

    @TableField("avatar")
    private String avatar = "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png";

    @TableField("status")
    private Integer status = 0;

    @TableField("created_at")
    private Date createdAt;

    @TableField("deleted_at")
    private Date deletedAt;
}