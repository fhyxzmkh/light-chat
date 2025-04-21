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
@TableName(value = "message", autoResultMap = true)
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private String uuid;

    @TableField("session_id")
    private String sessionId;

    @TableField("type")
    private Integer type;

    @TableField("content")
    private String content;

    @TableField("url")
    private String url;

    @TableField("send_id")
    private String sendId;

    @TableField("send_name")
    private String sendName;

    @TableField("send_avatar")
    private String sendAvatar;

    @TableField("receive_id")
    private String receiveId;

    @TableField("file_type")
    private String fileType;

    @TableField("file_name")
    private String fileName;

    @TableField("file_size")
    private String fileSize;

    @TableField("status")
    private Integer status;

    @TableField("created_at")
    private Date createdAt;

    @TableField("av_data")
    private String avData;
}