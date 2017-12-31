package com.jvdm.recruits.DataAccess;

import android.text.TextUtils;

import com.jvdm.recruits.Model.Group;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joske on 26/12/17.
 */

public class GroupAccess extends DataAccess {
    public static void add(Group group) throws NullPointerException {
        if (TextUtils.isEmpty(group.getName())) {
            throw new NullPointerException();
        }
        String key = database.child("groups").push().getKey();
        Map<String, Object> values = group.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + key, values);
        for (String uid : group.getRecruits().keySet()) {
            childUpdates.put("/recruit-groups/" + uid + "/" + key, values);
        }

        database.updateChildren(childUpdates);
    }
}
