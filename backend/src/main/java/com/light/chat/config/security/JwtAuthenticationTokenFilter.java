package com.light.chat.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.light.chat.domain.po.UserInfo;
import com.light.chat.mapper.UserInfoMapper;
import com.light.chat.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private UserInfoMapper userMapper;
 
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
 
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
 
        token = token.substring(7);
 
        String userUuid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userUuid = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", userUuid);
        UserInfo user = userMapper.selectOne(queryWrapper);
 
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
 
        UserDetailsImpl loginUser = new UserDetailsImpl(user);
 
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
 
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
 
        filterChain.doFilter(request, response);
    }
}