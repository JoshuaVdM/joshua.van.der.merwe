package com.jvdm.recruits.DataAccess;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.jvdm.recruits.Model.Permission;
import com.jvdm.recruits.Model.Recruit;

/**
 * Created by Joske on 23/12/17.
 */

public class RecruitAccess{

    public static void add(FirebaseUser currentUser, DatabaseReference database) {
        String displayname = currentUser.getDisplayName();
        if (displayname == null) {
            for (UserInfo profile: currentUser.getProviderData()) {
                if (!TextUtils.isEmpty(profile.getDisplayName())) {
                    displayname = profile.getDisplayName();
                    break;
                }
            }
        }
        Recruit r = new Recruit();
        r.setUsername(displayname);
        r.setEmail(currentUser.getEmail());
        r.setVerified(true);
        r.setPermissions(new Permission());
        r.getPermissions().setMember(true);
        r.setPhotoUri(currentUser.getPhotoUrl().toString());
        database.child("recruits").child(currentUser.getUid()).setValue(r);
    }

    /*public static void addRecruitIfNotExists() {
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
    }*/
}
