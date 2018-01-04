package com.jvdm.recruits.Model;

import android.content.Context;

import com.jvdm.recruits.MyApplication;
import com.jvdm.recruits.R;

/**
 * Created by Joske on 1/01/18.
 */

public enum Role implements IEnumSpinner {
    LEADER(R.string.group_member_role_leader),
    MEMBER(R.string.group_member_role_member);

    private int key;

    Role(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return MyApplication.getContext().getString(key);
    }

    @Override
    public String getLabel(Context context) {
        return context.getResources().getString(key);
    }
}
