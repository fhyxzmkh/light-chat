package com.light.chat.controller.group;

import com.light.chat.domain.po.GroupInfo;
import com.light.chat.service.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupInfoService groupInfoService;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody GroupInfo groupInfo) {
        return groupInfoService.createGroup(groupInfo);
    }

    @GetMapping("/load/my")
    public ResponseEntity<?> loadMyGroups() {
        return groupInfoService.loadMyGroups();
    }

}
