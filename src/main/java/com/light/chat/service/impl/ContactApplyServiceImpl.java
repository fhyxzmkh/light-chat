package com.light.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.entity.ContactApply;
import com.light.chat.mapper.ContactApplyMapper;
import com.light.chat.service.ContactApplyService;
import org.springframework.stereotype.Service;

@Service
public class ContactApplyServiceImpl extends ServiceImpl<ContactApplyMapper, ContactApply> implements ContactApplyService {
}