package com.jvdm.recruits.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.jvdm.recruits.Activities.GroupDetailActivity;
import com.jvdm.recruits.Adapters.GroupListAdapter;
import com.jvdm.recruits.DataAccess.DataAccess;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Dialog.AddGroupDialog;
import com.jvdm.recruits.MainActivity;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joske on 29/12/17.
 */

public class GroupsFragment extends Fragment {
    private View rootView;
    private ListView listView;
    private MainActivity mainActivity;
    private List<Group> values;
    private GroupListAdapter adapter;
    private Boolean admin = false;
    private FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            admin = getArguments().getBoolean("admin");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        mainActivity = (MainActivity) getActivity();
        listView = rootView.findViewById(R.id.list_groups);

        if (values == null) {
            values = new ArrayList<>();
        }

        setListListener();
        initFab();

        adapter = new GroupListAdapter(mainActivity, values);
        listView.setAdapter(adapter);

        return rootView;
    }

    private void initFab() {
        if (admin) {
            fab = rootView.findViewById(R.id.fab_add_group);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddGroupDialog(getContext(), new AddGroupDialog.onAddGroupDialogListener() {
                        @Override
                        public void onGroupAdded(final Group g) {
                            final DocumentReference groupRef = GroupAccess.getGroupDocumentReference(g.getKey());
                            DataAccess.getDatabase().runTransaction(new Transaction.Function<Integer>() {

                                @Nullable
                                @Override
                                public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                    DocumentSnapshot snapshot = transaction.get(groupRef);
                                    if (snapshot.exists()) {
                                        Group group = snapshot.toObject(Group.class);
                                        transaction.set(groupRef, group);
                                        return 1;
                                    } else {
                                        transaction.set(groupRef, g);
                                        return 0;
                                    }
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Integer>() {
                                @Override
                                public void onSuccess(Integer added) {
                                    if (added == 0) {
                                        Intent intent = new Intent(getContext(), GroupDetailActivity.class);
                                        intent.putExtra(GroupListAdapter.GROUP_KEY_INTENT, g.getKey());
                                        getContext().startActivity(intent);
                                    } else {
                                        Toast.makeText(mainActivity, "Error while adding group " + added, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mainActivity, "ERRORR", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void setListListener() {
        CollectionReference groupsRef;
        if (!admin) {
            groupsRef = RecruitAccess.getRecruitGroupsCollectionReference(mainActivity.currentUser.getUid());
            groupsRef.addSnapshotListener(mainActivity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                        Group g = dc.getDocument().toObject(Group.class);
                        g.setKey(dc.getDocument().getId());

                        switch (dc.getType()) {
                            case ADDED:
                                values.add(g);
                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                values.remove(g);
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            });
        } else {
            groupsRef = GroupAccess.getGroupsCollectionReference();
            groupsRef.addSnapshotListener(mainActivity, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                        Group g = dc.getDocument().toObject(Group.class);
                        g.setKey(dc.getDocument().getId());
                        switch (dc.getType()) {
                            case ADDED:
                                values.add(g);
                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                values.remove(g);
                                adapter.notifyDataSetChanged();
                                break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity.setTitle(admin ? "All groups" : "My groups");
    }
}
