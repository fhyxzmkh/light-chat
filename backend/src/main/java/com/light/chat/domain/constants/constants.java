package com.light.chat.domain.constants;

public class constants {

    // 验证码有效期（分钟）
    public static final long CAPTCHA_EXPIRE = 5;

    public static final String CHECK_CODE_KEY = "check_code_key";

    public static final String CHECK_CODE_KEY_EMAIL = "check_code_key_email";

    // 验证码Redis key
    public static final String CAPTCHA_KEY_PREFIX = "light:chat:captcha:";

    // 邮箱验证码Redis key
    public static final String EMAIL_CODE_PREFIX = "light:chat:email:code:";

}
