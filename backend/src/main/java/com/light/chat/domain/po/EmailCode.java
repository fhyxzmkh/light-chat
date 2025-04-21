package com.light.chat.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName(value = "email_code", autoResultMap = true)
public class EmailCode {
    @TableField("email")
    private String email;

    @TableField("code")
    private String code;

    @TableField(value = "create_at")
    private Date createAt;

    @TableField("status")
    private Integer status = 0;
}
