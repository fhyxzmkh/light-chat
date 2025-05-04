package com.light.chat.service;

import com.alibaba.fastjson.JSONArray;
import com.light.chat.domain.dto.group.DismissGroupRequest;
import com.light.chat.domain.dto.group.EnterGroupRequest;
import com.light.chat.domain.dto.group.LeaveGroupRequest;
import com.light.chat.domain.po.GroupInfo;
import org.springframework.http.ResponseEntity;

public interface GroupInfoService {

    String createGroup(GroupInfo groupInfo);

    JSONArray loadMyGroups();

    Integer checkGroupAddMode(String groupId);

    String enterGroupDirect(EnterGroupRequest request);

    String leaveGroup(LeaveGroupRequest leaveGroupRequest);

    String dismissGroup(DismissGroupRequest dismissGroupRequest);
}