package com.jvdm.recruits.DataAccess;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.jvdm.recruits.Model.Permission;
import com.jvdm.recruits.Model.Recruit;

/**
 * Created by Joske on 23/12/17.
 */

public class RecruitAccess extends DataAccess {
    @NonNull
    public static CollectionReference getRecruitsCollectionReference() {
        return getDatabase().collection("recruits");
    }

    public static DocumentReference getRecruitDocumentReference(String uid) {
        return getRecruitsCollectionReference().document(uid);
    }

    public static CollectionReference getRecruitGroupsCollectionReference(String uid) {
        return getRecruitDocumentReference(uid).collection("groups");
    }

    public static DocumentReference getRecruitGroupDocumentReference(String userUid, String groupUid) {
        return getRecruitGroupsCollectionReference(userUid).document(groupUid);
    }

    public static void add(FirebaseUser currentUser) {
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
        if (currentUser.getPhotoUrl() != null) {
            r.setPhotoUri(currentUser.getPhotoUrl().toString());
        }

        DocumentReference docRef = getRecruitDocumentReference(currentUser.getUid());
        docRef.set(r);
    }

    public static void updateRecruitVerified(String key, Boolean verified) {
        getRecruitDocumentReference(key).update("verified", true);
    }

    public static void declineRecruitVerification(String key) {
        getRecruitDocumentReference(key).delete();
    }

}
