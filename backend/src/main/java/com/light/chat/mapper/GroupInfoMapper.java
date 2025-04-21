package com.light.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.light.chat.domain.po.GroupInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupInfoMapper extends BaseMapper<GroupInfo> {
}