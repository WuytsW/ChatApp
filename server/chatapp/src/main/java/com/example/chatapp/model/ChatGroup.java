package com.example.chatapp.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chatgroups")
public class ChatGroup {

    public ChatGroup() {}
    public ChatGroup(String name, User creator) {
        this.name = name;
        this.creator = creator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToOne(optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "chatgroup_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "group_admins",
            joinColumns = @JoinColumn(name = "chatgroup_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> admins = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getMembers() {
        return members;
    }
    public void setMembers(List<User> members) {
        this.members = members;
    }
    public void addMember(User member){
        if (!this.members.contains(member)) {
            this.members.add(member);
        }
    }
    public void addMembers(List<User> members){
        if(members == null || members.isEmpty()) return;
        members.removeIf(member -> this.members.contains(member));
        this.members.addAll(members);
    }
    public void removeMember(User member){
        this.members.remove(member);
    }

    public List<User> getAdmins() {
        return admins;
    }
    public void setAdmins(List<User> admins) {
        this.admins = admins;
    }
    public void addAdmin(User admin){
        if (!this.admins.contains(admin)) {
            this.admins.add(admin);
        }
    }
    public void addMAdmins(List<User> admins){
        if(admins == null || admins.isEmpty()) return;
        admins.removeIf(admin -> this.admins.contains(admin));
        this.admins.addAll(admins);
    }
    public void removeAdmin(User admin){
        this.admins.remove(admin);
    }
}
