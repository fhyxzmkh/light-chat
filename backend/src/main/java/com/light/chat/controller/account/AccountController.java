package com.light.chat.controller.account;

import com.light.chat.controller.common.ABaseController;
import com.light.chat.domain.dto.account.RegisterOrLoginRequest;
import com.light.chat.domain.enums.ResponseCodeEnum;
import com.light.chat.domain.po.UserInfo;
import com.light.chat.domain.vo.ResponseVO;
import com.light.chat.exception.BusinessException;
import com.light.chat.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.light.chat.domain.constants.constants.CHECK_CODE_KEY;

@RestController
@RequestMapping("/account")
@Tag(name = "账号相关接口")
public class AccountController extends ABaseController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseVO register(@RequestBody RegisterOrLoginRequest registerRequest, HttpSession session) {
        String checkCode = registerRequest.getCheckCode();
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(CHECK_CODE_KEY))) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            return getSuccessResponseVO(userInfoService.register(registerRequest));
        } finally {
            session.removeAttribute(CHECK_CODE_KEY);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseVO login(HttpSession session, @RequestBody RegisterOrLoginRequest loginRequest) {
        String checkCode = loginRequest.getCheckCode();
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(CHECK_CODE_KEY))) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            return getSuccessResponseVO(userInfoService.login(loginRequest));
        } finally {
            session.removeAttribute(CHECK_CODE_KEY);
        }
    }

    @PostMapping("/profile/update")
    @Operation(summary = "更新用户信息")
    public ResponseVO updateUserInfo(@RequestBody UserInfo userInfo) {
        return getSuccessResponseVO(userInfoService.updateUserInfo(userInfo));
    }

    @GetMapping("/profile/get")
    @Operation(summary = "获取用户信息")
    public ResponseVO getUserInfo() {
        return getSuccessResponseVO(userInfoService.getUserInfo());
    }

}
