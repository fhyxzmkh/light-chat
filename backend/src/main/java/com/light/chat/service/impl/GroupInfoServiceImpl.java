package com.light.chat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.light.chat.domain.dto.group.DismissGroupRequest;
import com.light.chat.domain.dto.group.EnterGroupRequest;
import com.light.chat.domain.dto.group.LeaveGroupRequest;
import com.light.chat.domain.enums.ContactStatusEnum;
import com.light.chat.domain.enums.ContactTypeEnum;
import com.light.chat.domain.po.ContactApply;
import com.light.chat.domain.po.GroupInfo;
import com.light.chat.domain.po.Session;
import com.light.chat.domain.po.UserContact;
import com.light.chat.mapper.ContactApplyMapper;
import com.light.chat.mapper.GroupInfoMapper;
import com.light.chat.mapper.SessionMapper;
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

import java.util.Date;
import java.util.List;

@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfo> implements GroupInfoService {

    private final GroupInfoMapper groupInfoMapper;
    private final UserContactMapper userContactMapper;
    private final SessionMapper sessionMapper;
    private final ContactApplyMapper contactApplyMapper;

    public GroupInfoServiceImpl(GroupInfoMapper groupInfoMapper, UserContactMapper userContactMapper, SessionMapper sessionMapper, ContactApplyMapper contactApplyMapper) {
        this.groupInfoMapper = groupInfoMapper;
        this.userContactMapper = userContactMapper;
        this.sessionMapper = sessionMapper;
        this.contactApplyMapper = contactApplyMapper;
    }

    /**
     * 创建群聊
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
     * 获取我的群聊
     * @return
     */
    @Override
    public ResponseEntity<?> loadMyGroups() {
        // TODO：add redis

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
        // TODO：add redis

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> leaveGroup(LeaveGroupRequest leaveGroupRequest) {
        String groupId = leaveGroupRequest.getGroupId();
        String userId = leaveGroupRequest.getUserId();

        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(userId)) {
            return ResponseEntity.badRequest().body("Group ID and User ID cannot be empty");
        }

        // 从群组中移除用户
        GroupInfo groupInfo = groupInfoMapper.selectOne(
                new LambdaQueryWrapper<GroupInfo>()
                        .eq(GroupInfo::getUuid, groupId)
        );

        if (groupInfo == null) {
            return ResponseEntity.badRequest().body("Group not found");
        }

        JSONArray members = JSON.parseArray(groupInfo.getMembers());
        if (!members.contains(userId)) {
            return ResponseEntity.badRequest().body("User not in group");
        }
        members.remove(userId);

        groupInfo.setMembers(members.toJSONString());
        groupInfo.setMemberCnt(groupInfo.getMemberCnt() - 1);

        groupInfoMapper.updateById(groupInfo);

        Date now = new Date();

        // 删除相关会话
        Session session = new Session();
        session.setDeletedAt(now);
        if (sessionMapper.update(session,
                new LambdaUpdateWrapper<Session>()
                        .eq(Session::getSendId, userId)
                        .eq(Session::getReceiveId, groupId)
        ) <= 0) {
            return ResponseEntity.badRequest().body("Failed to delete session");
        }

        // 更新联系人状态
        if (userContactMapper.update(null,
                new LambdaUpdateWrapper<UserContact>()
                        .eq(UserContact::getUserId, userId)
                        .eq(UserContact::getContactId, groupId)
                        .set(UserContact::getDeletedAt, now)
                        .set(UserContact::getStatus, ContactStatusEnum.QUIT_GROUP.getCode())
        ) <= 0) {
            return ResponseEntity.badRequest().body("Failed to update contact status");
        }

        // 删除申请记录
        if (contactApplyMapper.update(null,
                new LambdaUpdateWrapper<ContactApply>()
                        .eq(ContactApply::getUserId, userId)
                        .eq(ContactApply::getContactId, groupId)
                        .set(ContactApply::getDeletedAt, now)
        ) <= 0) {
            return ResponseEntity.badRequest().body("Failed to delete contact apply");
        }

        // TODO：清理redis缓存

        return ResponseEntity.ok("Leave group successfully");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> dismissGroup(DismissGroupRequest dismissGroupRequest) {
        String groupId = dismissGroupRequest.getGroupId();
        String ownerId = dismissGroupRequest.getOwnerId();

        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(ownerId)) {
            return ResponseEntity.badRequest().body("Group ID and Owner ID cannot be empty");
        }

        Date now = new Date();

        // 软删除群组
        if (groupInfoMapper.update(null,
                new LambdaUpdateWrapper<GroupInfo>()
                        .eq(GroupInfo::getUuid, groupId)
                        .set(GroupInfo::getDeletedAt, now)
        ) <= 0) {
            return ResponseEntity.badRequest().body("Failed to dismiss group");
        }

        // 软删除群组相关的会话
        List<Session> sessionList = sessionMapper.selectList(
                new LambdaQueryWrapper<Session>()
                        .eq(Session::getReceiveId, groupId)
        );

        for (Session session : sessionList) {
            session.setDeletedAt(now);
            sessionMapper.updateById(session);
        }

        // 软删除用户联系人记录
        List<UserContact> userContactList = userContactMapper.selectList(
                new LambdaQueryWrapper<UserContact>()
                        .eq(UserContact::getContactId, groupId)
        );

        for (UserContact userContact : userContactList) {
            userContact.setDeletedAt(now);
            userContact.setStatus(ContactStatusEnum.QUIT_GROUP.getCode());
            userContactMapper.updateById(userContact);
        }

        // 软删除群组相关的申请记录
        List<ContactApply> contactApplyList = contactApplyMapper.selectList(
                new LambdaQueryWrapper<ContactApply>()
                        .eq(ContactApply::getContactId, groupId)
        );

        for (ContactApply contactApply : contactApplyList) {
            contactApply.setDeletedAt(now);
            contactApplyMapper.updateById(contactApply);
        }

        // TODO：清理redis缓存

        return ResponseEntity.ok("Dismiss group successfully");
    }

}