package com.jvdm.recruits.DataAccess;

import android.text.TextUtils;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jvdm.recruits.Model.Recruit;

/**
 * Created by Joske on 23/12/17.
 */

public class RecruitAccess extends DataAccess {

    public static void add() {
        String displayname = currentUser.getDisplayName();
        if (displayname == null) {
            for (UserInfo profile: currentUser.getProviderData()) {
                if (!TextUtils.isEmpty(profile.getDisplayName())) {
                    displayname = profile.getDisplayName();
                    break;
                }
            }
        }
        Recruit r = new Recruit(displayname, currentUser.getEmail());
        database.child("recruits").child(currentUser.getUid()).setValue(r);
    }

    public static void addRecruitIfNotExists() {
        updateAuth();
        ValueEventListener recruitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recruit r = dataSnapshot.getValue(Recruit.class);
                if (r == null) {
                    add();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        DatabaseReference user = database.child("recruits").child(currentUser.getUid());
        user.addListenerForSingleValueEvent(recruitListener);
    }
}
