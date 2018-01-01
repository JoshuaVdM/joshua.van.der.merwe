package com.jvdm.recruits.Model;

import com.google.firebase.firestore.DocumentReference;

/**
 * Created by Joske on 1/01/18.
 */

public class GroupMember {
    private DocumentReference recruitReference;
    private Permission permissions;
    private Role role;
    private Boolean verified;

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

    public Permission getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission permissions) {
        this.permissions = permissions;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
