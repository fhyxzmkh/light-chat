package com.light.chat.config.security;

import com.light.chat.domain.po.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
 
    private UserInfo user;
 
    public UserDetailsImpl(UserInfo user) {
        this.user = user;
    }
 
    public UserInfo getUser() {
        return user;
    }
 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add((GrantedAuthority) () -> switch (user.getIsAdmin()) {
            case 0 -> "USER";
            case 1 -> "ADMIN";
            default -> throw new IllegalStateException("Unexpected value: " + user.getIsAdmin());
        });
 
        return list;
    }
 
    @Override
    public String getPassword() {
        return user.getPassword();
    }
 
    @Override
    public String getUsername() {
        return user.getNickname();
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return true;
    }
}