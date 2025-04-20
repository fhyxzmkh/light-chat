package com.light.chat.controller.common;

import com.light.chat.service.EmailCodeService;
import com.light.chat.utils.CaptchaUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.light.chat.domain.constants.constants.*;

@RestController
public class CaptchaController {

    @Autowired
    private EmailCodeService emailCodeService;

    /**
     * 生成验证码图片
     * @param type null或者0为默认验证码，1为邮箱验证码
     */
    @GetMapping("/checkCode")
    public void generateCaptcha(HttpSession session,
                                HttpServletResponse response,
                                @RequestParam Integer type) {
        try {
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            String captchaCode = CaptchaUtil.generateNumberCode(5);

            if (type == null || type == 0) {
                session.setAttribute(CHECK_CODE_KEY, captchaCode);
            }
            else {
                session.setAttribute(CHECK_CODE_KEY_EMAIL, captchaCode);
            }

            CaptchaUtil.generateImage(captchaCode, response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("生成验证码失败", e);
        }
    }

    /**
     * 获取邮箱验证码
     * @param type 0为注册，1为找回密码
     */
    @GetMapping("/emailCode")
    public ResponseEntity<?> sendEmailCode(HttpSession session,
                                           @RequestParam String email,
                                           @RequestParam String checkCode,
                                           @RequestParam Integer type) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(CHECK_CODE_KEY_EMAIL))) {
                return ResponseEntity.badRequest().body("验证码错误");
            }

            return emailCodeService.sendEmailCode(email, type);
        } finally {
            session.removeAttribute(CHECK_CODE_KEY_EMAIL);
        }

    }

}
