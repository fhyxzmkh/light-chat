package com.light.chat.service;

import com.light.chat.domain.po.GroupInfo;
import org.springframework.http.ResponseEntity;

public interface GroupInfoService {

    ResponseEntity<?> createGroup(GroupInfo groupInfo);

    ResponseEntity<?> loadMyGroups();
}