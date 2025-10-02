package com.example.chatapp.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "group_messages")
public class GroupMessage {

    public GroupMessage(){

    }
    public GroupMessage(Message message, ChatGroup chatGroup){
        this.message = message;
        this.chatGroup = chatGroup;
    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ChatGroup chatGroup;

    @ManyToMany
    @JoinTable(
            name = "read_by_members",
            joinColumns = @JoinColumn(name = "group_message_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> read_by_members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }

    public ChatGroup getChatGroup() {
        return chatGroup;
    }
    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    public List<User> getRead_by_members() {
        return read_by_members;
    }
    public void setRead_by_Members(List<User> members) {
        this.read_by_members = members;
    }
    public void addRead_by_Member(User member){
        this.read_by_members.add(member);
    }
    public void addRead_by_Members(List<User> members){
        this.read_by_members.addAll(members);
    }
    public void removeRead_by_Member(User member){
        this.read_by_members.remove(member);
    }


}
