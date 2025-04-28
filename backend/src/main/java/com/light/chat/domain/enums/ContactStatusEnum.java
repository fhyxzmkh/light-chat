package com.light.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContactStatusEnum {

    // 联系状态，0.正常，1.拉黑，2.被拉黑，3.删除好友，4.被删除好友，5.被禁言，6.退出群聊，7.被踢出群聊

    NORMAL(0),
    BE_BLACK(1),
    BLACK(2),
    BE_DELETE(3),
    DELETE(4),
    SILENCE(5),
    QUIT_GROUP(6),
    KICK_OUT_GROUP(7);

    private final int code;

    public static ContactStatusEnum fromCode(int code) {
        for (ContactStatusEnum type : ContactStatusEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EntityType code: " + code);
    }

}
