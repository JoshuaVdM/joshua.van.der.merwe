package com.jvdm.recruits.Model;

import android.content.Context;

import com.google.firebase.firestore.Exclude;
import com.jvdm.recruits.MyApplication;
import com.jvdm.recruits.R;

public enum InvitationState implements IEnumSpinner {
    PENDING(R.string.group_member_invitation_pending),
    ACCEPTED(R.string.group_member_invitation_accepted),
    DECLINED(R.string.group_member_invitation_declined);

    private int key;

    InvitationState(int key) {
        this.key = key;
    }

    public String getEnumValue() {
        return super.toString();
    }

    @Override
    @Exclude
    public String toString() {
        return MyApplication.getContext().getString(key);
    }

    @Override
    public String getLabel(Context context) {
        return context.getResources().getString(key);
    }
}
