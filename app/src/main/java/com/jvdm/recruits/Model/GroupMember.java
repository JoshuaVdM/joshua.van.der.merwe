package com.jvdm.recruits.Model;

import com.google.firebase.firestore.DocumentReference;

/**
 * Created by Joske on 1/01/18.
 */

public class GroupMember {
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
}
