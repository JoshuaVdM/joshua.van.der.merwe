package com.jvdm.recruits.Model;

/**
 * Created by Joske on 5/01/18.
 */

public class RecruitGroup {
    private Group group;
    private GroupMember member;

    public RecruitGroup() {
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupMember getMember() {
        return member;
    }

    public void setMember(GroupMember member) {
        this.member = member;
    }
}
