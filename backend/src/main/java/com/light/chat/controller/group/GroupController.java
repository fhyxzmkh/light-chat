package com.light.chat.controller.group;

import com.light.chat.domain.dto.group.DismissGroupRequest;
import com.light.chat.domain.dto.group.EnterGroupRequest;
import com.light.chat.domain.dto.group.LeaveGroupRequest;
import com.light.chat.domain.po.GroupInfo;
import com.light.chat.service.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupInfoService groupInfoService;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody GroupInfo groupInfo) {
        return groupInfoService.createGroup(groupInfo);
    }

    @GetMapping("/load/myGroups")
    public ResponseEntity<?> loadMyGroups() {
        return groupInfoService.loadMyGroups();
    }

    @GetMapping("/check/groupAddMode")
    public ResponseEntity<?> checkGroupAddMode(@RequestParam String groupId) {
        return groupInfoService.checkGroupAddMode(groupId);
    }

    @PostMapping("/enter/direct")
    public ResponseEntity<?> enterGroupDirect(@RequestBody EnterGroupRequest request) {
        return groupInfoService.enterGroupDirect(request);
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leaveGroup(@RequestBody LeaveGroupRequest leaveGroupRequest) {
        return groupInfoService.leaveGroup(leaveGroupRequest);
    }

    @PostMapping("/dismiss")
    public ResponseEntity<?> dismissGroup(@RequestBody DismissGroupRequest dismissGroupRequest) {
        return groupInfoService.dismissGroup(dismissGroupRequest);
    }

}
