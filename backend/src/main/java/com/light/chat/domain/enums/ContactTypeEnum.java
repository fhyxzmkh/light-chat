package com.light.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContactTypeEnum {

    // 联系类型，0.用户，1.群聊

    USER(0),
    GROUP(1);

    private final int code;

    public static ContactTypeEnum fromCode(int code) {
        for (ContactTypeEnum type : ContactTypeEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EntityType code: " + code);
    }
}
