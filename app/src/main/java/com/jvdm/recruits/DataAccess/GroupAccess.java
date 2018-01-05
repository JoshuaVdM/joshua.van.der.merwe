package com.jvdm.recruits.DataAccess;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.Model.GroupMember;
import com.jvdm.recruits.Model.InvitationState;
import com.jvdm.recruits.Model.RecruitGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joske on 26/12/17.
 */

public class GroupAccess extends DataAccess {
    private static final String TAG = GroupAccess.class.getName();

    public static CollectionReference getGroupsCollectionReference() {
        return getDatabase().collection("groups");
    }

    public static DocumentReference getGroupDocumentReference(String uid) {
        return getGroupsCollectionReference().document(uid);
    }

    public static CollectionReference getGroupMembersCollectionReference(String uid) {
        return getGroupDocumentReference(uid).collection("members");
    }

    public static DocumentReference getGroupMemberDocumentReference(String groupUid,
                                                                    String recruitUid) {
        return getGroupMembersCollectionReference(groupUid).document(recruitUid);
    }

    public static void updateGroup(final Group group) {
        final DocumentReference groupDocRef = getGroupDocumentReference(group.getKey());

        getDatabase().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(groupDocRef);
                if (snapshot.exists()) {
                    transaction.set(groupDocRef, group);
                }
                return null;
            }
        });
    }

    public static void updateGroupMemberInvitationState(Context context, String groupName, String uid, InvitationState state) {
        getGroupMemberDocumentReference(groupName, uid).update(
                "state", state.getEnumValue()
        );
        RecruitAccess.getRecruitGroupDocumentReference(uid, groupName).update(
                "member.state", state.getEnumValue()
        );
    }

    public static void updateOrAddGroupMembers(
            final String name,
            final HashMap<String, GroupMember> members) {
        final DocumentReference groupDocRef = getGroupDocumentReference(name);

        getDatabase().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(groupDocRef);
                if (!snapshot.exists()) {
                    Log.d(TAG, "Group doesn't exist");
                } else {
                    transaction.set(groupDocRef, snapshot.toObject(Group.class));
                    for (Map.Entry<String, GroupMember> member : members.entrySet()) {
                        transaction.set(getGroupMemberDocumentReference(
                                name,
                                member.getKey()),
                                member.getValue()
                        );

                        /*HashMap<String, DocumentReference> recruitGroup = new HashMap<>();
                        recruitGroup.put("group", groupDocRef);*/

                        RecruitGroup recruitGroup = new RecruitGroup();
                        recruitGroup.setGroup(snapshot.toObject(Group.class));
                        recruitGroup.setMember(member.getValue());

                        transaction.set(
                                RecruitAccess.getRecruitGroupDocumentReference(
                                        member.getKey(),
                                        name),
                                recruitGroup);
                    }
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure");
            }
        });
    }

    public static void deleteGroupMembers(final String name, final ArrayList<String> members) {
        final DocumentReference groupDocRef = getGroupDocumentReference(name);

        getDatabase().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(groupDocRef);
                if (!snapshot.exists()) {
                    Log.d(TAG, "Group doesn't exist");
                } else {
                    transaction.set(groupDocRef, snapshot.toObject(Group.class));
                    for (String uid : members) {
                        transaction.delete(getGroupMemberDocumentReference(name, uid));
                        transaction.delete(RecruitAccess.
                                getRecruitGroupDocumentReference(uid, name));
                    }
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failure");
            }
        });
    }
}
