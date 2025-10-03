package com.example.chatapp.dto.request;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupRequest {
    private String groupName;
    private List<Long> newMembersIds = new ArrayList<>();
    private List<Long> adminIds =  new ArrayList<>();

    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Long> getNewMembersIds() {
        return newMembersIds;
    }
    public void setNewMembersIds(List<Long> newMembersIds) {
        this.newMembersIds = newMembersIds;
    }

    public List<Long> getAdminIds() {
        return adminIds;
    }
    public void setAdminIds(List<Long> adminIds) {
        this.adminIds = adminIds;
    }
}
