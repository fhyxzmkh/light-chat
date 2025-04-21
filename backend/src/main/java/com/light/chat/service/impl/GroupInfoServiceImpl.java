package com.light.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.po.GroupInfo;
import com.light.chat.mapper.GroupInfoMapper;
import com.light.chat.service.GroupInfoService;
import org.springframework.stereotype.Service;

@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements GroupInfoService {
}