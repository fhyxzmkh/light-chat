package com.light.chat.utils;

import com.light.chat.config.security.UserDetailsImpl;
import com.light.chat.domain.po.UserInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static UserInfo getLoginUser() {

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();

        return loginUser.getUser();
    }

}
