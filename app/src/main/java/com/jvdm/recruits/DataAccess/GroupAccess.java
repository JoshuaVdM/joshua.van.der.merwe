package com.jvdm.recruits.DataAccess;

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

    public static void add(final Group group) {
        add(group, new HashMap<String, GroupMember>());
    }

    public static void add(final Group group, final HashMap<String, GroupMember> members) {
        // Get reference to group
        final DocumentReference groupDocRef = getGroupDocumentReference(group.getKey());

        // Run transaction
        getDatabase().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                // Get the group at the referenced location
                DocumentSnapshot snapshot = transaction.get(groupDocRef);

                if (snapshot.exists()) {
                    // Group already exists
                    Log.d(TAG, "Group already exists");
                } else {
                    // Group doesn't exist yet, set group at referenced location
                    transaction.set(groupDocRef, group);

                    // Iterate over members
                    for (Map.Entry<String, GroupMember> member : members.entrySet()) {
                        // Set group member
                        transaction.set(getGroupMemberDocumentReference(
                                group.getKey(),
                                member.getKey()),
                                member.getValue()
                        );

                        // Make recruitgroup object, to be put in recruit groups collection
                        HashMap<String, DocumentReference> recruitGroup = new HashMap<>();
                        recruitGroup.put("group", groupDocRef);

                        transaction.set(RecruitAccess.getRecruitGroupDocumentReference(
                                member.getKey(),
                                group.getKey()),
                                recruitGroup
                        );
                    }
                }
                return null;
            }
        });
    }

    public static void updateGroup(final String name, final Group group) {
        final DocumentReference groupDocRef = getGroupDocumentReference(name);

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

                        HashMap<String, DocumentReference> recruitGroup = new HashMap<>();
                        recruitGroup.put("group", groupDocRef);

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
