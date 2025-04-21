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
@TableName(value = "contact_apply", autoResultMap = true)
public class ContactApply {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("uuid")
    private String uuid;

    @TableField("user_id")
    private String userId;

    @TableField("contact_id")
    private String contactId;

    @TableField("contact_type")
    private Integer contactType;

    @TableField("status")
    private Integer status;

    @TableField("message")
    private String message;

    @TableField("last_apply_at")
    private Date lastApplyAt;

    @TableField("deleted_at")
    private Date deletedAt;
}