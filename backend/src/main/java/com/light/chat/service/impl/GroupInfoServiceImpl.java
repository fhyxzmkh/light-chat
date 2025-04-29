package com.light.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.dto.group.EnterGroupRequest;
import com.light.chat.domain.enums.ContactStatusEnum;
import com.light.chat.domain.enums.ContactTypeEnum;
import com.light.chat.domain.po.GroupInfo;
import com.light.chat.domain.po.UserContact;
import com.light.chat.mapper.GroupInfoMapper;
import com.light.chat.mapper.UserContactMapper;
import com.light.chat.service.GroupInfoService;
import com.light.chat.utils.CaptchaUtil;
import com.light.chat.utils.ObjectUtil;
import com.light.chat.utils.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements GroupInfoService {

    private final GroupInfoMapper groupInfoMapper;
    private final UserContactMapper userContactMapper;

    public GroupInfoServiceImpl(GroupInfoMapper groupInfoMapper, UserContactMapper userContactMapper) {
        this.groupInfoMapper = groupInfoMapper;
        this.userContactMapper = userContactMapper;
    }

    /**
     * Create a new group
     * @param groupInfo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> createGroup(GroupInfo groupInfo) {
        if (StringUtils.isBlank(groupInfo.getName())) {
            return ResponseEntity.badRequest().body("Group name cannot be empty");
        }

        GroupInfo newGroup = new GroupInfo();
        BeanUtils.copyProperties(groupInfo, newGroup, ObjectUtil.getNullPropertyNames(groupInfo));
        newGroup.setUuid(CaptchaUtil.generateNumberCode(20));
        newGroup.setOwnerId(SecurityUtil.getLoginUser().getUuid());
        newGroup.setMembers(JSON.toJSONString(List.of(newGroup.getOwnerId())));

        groupInfoMapper.insert(newGroup);

        UserContact userContact = UserContact.builder()
                .userId(newGroup.getOwnerId())
                .contactId(newGroup.getUuid())
                .contactType(ContactTypeEnum.GROUP.getCode())
                .status(ContactStatusEnum.NORMAL.getCode())
                .build();

        userContactMapper.insert(userContact);

        return ResponseEntity.ok("Group created successfully");
    }

    /**
     * Load my groups
     * @return
     */
    @Override
    public ResponseEntity<?> loadMyGroups() {
        QueryWrapper<GroupInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", SecurityUtil.getLoginUser().getUuid());
        queryWrapper.eq("status", 0);
        queryWrapper.eq("deleted_at", null);
        queryWrapper.orderByDesc("created_at");
        List<GroupInfo> groupInfos = groupInfoMapper.selectList(queryWrapper);

        return ResponseEntity.ok(new JSONArray(groupInfos));
    }

    /**
     * Check if the group is in add mode
     * @param groupId
     * @return
     */
    @Override
    public ResponseEntity<?> checkGroupAddMode(String groupId) {
        QueryWrapper<GroupInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", groupId);
        GroupInfo groupInfo = groupInfoMapper.selectOne(queryWrapper);

        if (groupInfo == null) {
            return ResponseEntity.badRequest().body("Group not found");
        }

        return ResponseEntity.ok(groupInfo.getAddMode());
    }

    /**
     * Enter group directly
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> enterGroupDirect(EnterGroupRequest request) {
        if (StringUtils.isBlank(request.getGroupId())) {
            return ResponseEntity.badRequest().body("Group ID cannot be empty");
        }

        if (StringUtils.isBlank(request.getContactId())) {
            return ResponseEntity.badRequest().body("Contact ID cannot be empty");
        }

        QueryWrapper<GroupInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", request.getGroupId());
        GroupInfo groupInfo = groupInfoMapper.selectOne(queryWrapper);

        if (groupInfo == null) {
            return ResponseEntity.badRequest().body("Group not found");
        }

        JSONArray members = JSON.parseArray(groupInfo.getMembers());

        if (members.contains(request.getContactId())) {
            return ResponseEntity.badRequest().body("User already in group");
        }

        members.add(request.getContactId());

        groupInfo.setMembers(members.toJSONString());
        groupInfo.setMemberCnt(groupInfo.getMemberCnt() + 1);

        groupInfoMapper.updateById(groupInfo);

        UserContact userContact = UserContact.builder()
                .userId(request.getContactId())
                .contactId(request.getGroupId())
                .contactType(ContactTypeEnum.GROUP.getCode())
                .status(ContactStatusEnum.NORMAL.getCode())
                .build();

        userContactMapper.insert(userContact);

        return ResponseEntity.ok("Entered group successfully");
    }

}