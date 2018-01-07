package com.jvdm.recruits.Model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

/**
 * Created by Joske on 1/01/18.
 */

public class GroupMember {
    @Exclude
    private String key;
    private DocumentReference recruitReference;
    private Role role;
    private InvitationState state;

    // Constructors
    public GroupMember() {
    }

    // Getters and setters
    public DocumentReference getRecruitReference() {
        return recruitReference;
    }

    public void setRecruitReference(DocumentReference recruitReference) {
        this.recruitReference = recruitReference;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public InvitationState getState() {
        return state;
    }

    public void setState(InvitationState state) {
        this.state = state;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupMember that = (GroupMember) o;

        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
