package com.light.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.entity.UserContact;
import com.light.chat.mapper.UserContactMapper;
import com.light.chat.service.UserContactService;
import org.springframework.stereotype.Service;

@Service
public class UserContactServiceImpl extends ServiceImpl<UserContactMapper, UserContact> implements UserContactService {
}