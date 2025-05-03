package com.light.chat.service;

import com.light.chat.domain.dto.group.DismissGroupRequest;
import com.light.chat.domain.dto.group.EnterGroupRequest;
import com.light.chat.domain.dto.group.LeaveGroupRequest;
import com.light.chat.domain.po.GroupInfo;
import org.springframework.http.ResponseEntity;

public interface GroupInfoService {

    ResponseEntity<?> createGroup(GroupInfo groupInfo);

    ResponseEntity<?> loadMyGroups();

    ResponseEntity<?> checkGroupAddMode(String groupId);

    ResponseEntity<?> enterGroupDirect(EnterGroupRequest request);

    ResponseEntity<?> leaveGroup(LeaveGroupRequest leaveGroupRequest);

    ResponseEntity<?> dismissGroup(DismissGroupRequest dismissGroupRequest);
}