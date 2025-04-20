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
@TableName(value = "user_contact", autoResultMap = true)
public class UserContact {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("contact_id")
    private String contactId;

    @TableField("contact_type")
    private Integer contactType;

    @TableField("status")
    private Integer status;

    @TableField("created_at")
    private Date createdAt;

    @TableField("deleted_at")
    private Date deletedAt;
}