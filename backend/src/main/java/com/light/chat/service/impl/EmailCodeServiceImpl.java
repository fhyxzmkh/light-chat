package com.light.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.entity.EmailCode;
import com.light.chat.domain.entity.UserInfo;
import com.light.chat.mapper.EmailCodeMapper;
import com.light.chat.mapper.UserInfoMapper;
import com.light.chat.service.EmailCodeService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.light.chat.utils.CaptchaUtil.generateNumberCode;

@Service
public class EmailCodeServiceImpl extends ServiceImpl<EmailCodeMapper, EmailCode> implements EmailCodeService {

    private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private EmailCodeMapper emailCodeMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sendUserName;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> sendEmailCode(String email, Integer type) {
        if (type == 0) {
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("email", email);
            UserInfo user = userInfoMapper.selectOne(wrapper);
            if (user != null) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            String code = generateNumberCode(5);

            sendEmailCode(email, code); // 发送验证码

            emailCodeMapper.disableEmailCode(email); // 将之前的验证码设置为无效

            EmailCode emailCode = EmailCode.builder().email(email).code(code).build();
            emailCodeMapper.insert(emailCode);

            return ResponseEntity.ok("Email code sent successfully");
        }

        return ResponseEntity.badRequest().body("Invalid type");
    }

    private void sendEmailCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sendUserName);
            helper.setTo(toEmail);

            helper.setSubject("light-chat 邮箱验证码");
            helper.setText("您好，您的邮箱验证码是：" + code + "，请在5分钟内完成验证。", true);
            helper.setSentDate(new Date());

            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("邮件发送失败；{}", e.getMessage());
            throw new RuntimeException("邮件发送失败");
        }
    }

}