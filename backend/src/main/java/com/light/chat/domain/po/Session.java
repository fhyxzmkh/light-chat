package com.light.chat.domain.po;

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
@TableName(value = "session", autoResultMap = true)
public class Session {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private String uuid;

    @TableField("send_id")
    private String sendId;

    @TableField("receive_id")
    private String receiveId;

    @TableField("receive_name")
    private String receiveName;

    @TableField("avatar")
    private String avatar = "default_avatar.png";

    @TableField("created_at")
    private Date createdAt;

    @TableField("deleted_at")
    private Date deletedAt;
}