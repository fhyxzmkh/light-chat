package com.light.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.light.chat.domain.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}