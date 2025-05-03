package com.light.chat.domain.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveGroupRequest {
    private String userId;
    private String groupId;
}
