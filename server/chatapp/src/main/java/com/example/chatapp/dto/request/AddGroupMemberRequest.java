package com.example.chatapp.dto.request;

public class AddGroupMemberRequest {
    private String group_name;
    private String member_name;

    public String getGroup_name() {
        return group_name;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getMember_name() {
        return member_name;
    }
    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }
}
