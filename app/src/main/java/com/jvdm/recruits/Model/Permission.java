package com.jvdm.recruits.Model;

/**
 * Created by Joske on 31/12/17.
 */

public class Permission {
    private boolean admin;
    private boolean member;

    public Permission() {
    }

    public Permission(boolean admin, boolean member) {
        this.admin = admin;
        this.member = member;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

}