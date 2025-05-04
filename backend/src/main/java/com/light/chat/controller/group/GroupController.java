package com.light.chat.controller.group;

import com.light.chat.controller.common.ABaseController;
import com.light.chat.domain.dto.group.DismissGroupRequest;
import com.light.chat.domain.dto.group.EnterGroupRequest;
import com.light.chat.domain.dto.group.LeaveGroupRequest;
import com.light.chat.domain.po.GroupInfo;
import com.light.chat.domain.vo.ResponseVO;
import com.light.chat.service.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/group")
public class GroupController extends ABaseController {

    @Autowired
    private GroupInfoService groupInfoService;

    @PostMapping("/create")
    public ResponseVO createGroup(@RequestBody GroupInfo groupInfo) {
        return getSuccessResponseVO(groupInfoService.createGroup(groupInfo));
    }

    @GetMapping("/load/myGroups")
    public ResponseVO loadMyGroups() {
        return getSuccessResponseVO(groupInfoService.loadMyGroups());
    }

    @GetMapping("/check/groupAddMode")
    public ResponseVO checkGroupAddMode(@RequestParam String groupId) {
        return getSuccessResponseVO(groupInfoService.checkGroupAddMode(groupId));
    }

    @PostMapping("/enter/direct")
    public ResponseVO enterGroupDirect(@RequestBody EnterGroupRequest request) {
        return getSuccessResponseVO(groupInfoService.enterGroupDirect(request));
    }

    @PostMapping("/leave")
    public ResponseVO leaveGroup(@RequestBody LeaveGroupRequest leaveGroupRequest) {
        return getSuccessResponseVO(groupInfoService.leaveGroup(leaveGroupRequest));
    }

    @PostMapping("/dismiss")
    public ResponseVO dismissGroup(@RequestBody DismissGroupRequest dismissGroupRequest) {
        return getSuccessResponseVO(groupInfoService.dismissGroup(dismissGroupRequest));
    }

}
