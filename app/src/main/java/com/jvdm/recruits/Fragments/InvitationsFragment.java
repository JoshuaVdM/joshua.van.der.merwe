package com.jvdm.recruits.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import com.jvdm.recruits.Dialogs.AcceptInvitationDialog;
import com.jvdm.recruits.Dialogs.AddGroupDialog;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.Model.InvitationState;
import com.jvdm.recruits.Model.RecruitGroup;
import com.jvdm.recruits.Properties;
import com.jvdm.recruits.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joske on 29/12/17.
 */

public class InvitationsFragment extends Fragment {
    private ListView listView;
    private List<Group> values;
    private GroupListAdapter adapter;
    private Boolean admin = false;
    private FloatingActionButton fab;

    private View.OnClickListener onListItemClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String name = ((TextView) v.findViewById(R.id.txt_group_name)).getText().toString();
            new AcceptInvitationDialog(
                    getContext(),
                    name,
                    new AcceptInvitationDialog.onAcceptInvitationDialogInteractionListener() {
                        @Override
                        public void onInvitationAccepted() {
                            GroupAccess.updateGroupMemberInvitationState(
                                    getContext(),
                                    name,
                                    Properties.getInstance().getCurrentRecruit().getUid(),
                                    InvitationState.ACCEPTED
                            );
                            Toast.makeText(
                                    getContext(),
                                    R.string.group_member_invitation_accepted,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        @Override
                        public void onInvitationDeclined() {
                            GroupAccess.updateGroupMemberInvitationState(
                                    getContext(),
                                    name,
                                    Properties.getInstance().getCurrentRecruit().getUid(),
                                    InvitationState.DECLINED
                            );
                            Toast.makeText(
                                    getContext(),
                                    R.string.group_member_invitation_declined,
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            admin = getArguments().getBoolean("admin");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        listView = rootView.findViewById(R.id.list_groups);

        if (values == null) {
            values = new ArrayList<>();
        }

        setGroupsListener();
        initFab(rootView);

        adapter = new GroupListAdapter(getActivity(), values, onListItemClickedListener);
        listView.setAdapter(adapter);

        return rootView;
    }

    private void initFab(View rootView) {
        if (admin) {
            fab = rootView.findViewById(R.id.fab_add_group);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddGroupDialog(getContext(), new AddGroupDialog.onAddGroupDialogListener() {
                        @Override
                        public void onGroupChosen(final Group g) {
                            final DocumentReference groupRef;
                            groupRef = GroupAccess.getGroupDocumentReference(g.getKey());
                            DataAccess.getDatabase()
                                    .runTransaction(new Transaction.Function<Integer>() {

                                        @Nullable
                                        @Override
                                        public Integer apply(@NonNull Transaction transaction)
                                                throws FirebaseFirestoreException {
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
                                        Intent intent = new Intent(getContext(),
                                                GroupDetailActivity.class);
                                        intent.putExtra(GroupListAdapter.GROUP_KEY_INTENT,
                                                g.getKey());
                                        getContext().startActivity(intent);
                                    } else {
                                        Toast.makeText(getActivity(),
                                                "Error while adding group " + added,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(),
                                            "ERRORR",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void setGroupsListener() {
        CollectionReference groupsRef;
        groupsRef = RecruitAccess.
                getRecruitGroupsCollectionReference(
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                );
        groupsRef.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    RecruitGroup g = dc.getDocument().toObject(RecruitGroup.class);
                    g.getGroup().setKey(dc.getDocument().getId());

                    switch (dc.getType()) {
                        case ADDED:
                            if (g.getMember().getState() == InvitationState.PENDING) {
                                values.add(g.getGroup());
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case MODIFIED:
                            values.remove(g.getGroup());
                            if (g.getMember().getState() == InvitationState.PENDING) {
                                values.add(g.getGroup());
                            }
                            adapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            values.remove(g.getGroup());
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(admin ? "All groups" : "My groups");
    }
}
