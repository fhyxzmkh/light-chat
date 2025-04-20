package com.light.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.light.chat.domain.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}