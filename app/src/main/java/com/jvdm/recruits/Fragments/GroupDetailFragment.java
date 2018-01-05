package com.jvdm.recruits.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jvdm.recruits.Adapters.MemberListAdapter;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Dialogs.AddGroupMemberDialog;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.Model.GroupMember;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.Model.Role;
import com.jvdm.recruits.Properties;
import com.jvdm.recruits.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joske on 03/01/2018.
 */

public class GroupDetailFragment extends Fragment {
    private String groupUid;

    private TextView name;
    private TextView city;
    private ImageView image;
    private ListView listView;
    private GroupMember currentMember;
    private FloatingActionButton fab;

    private onGroupDetailFragmentInteractionListener listener;
    private List<GroupMember> groupMembers;
    private MemberListAdapter adapter;

    private AddGroupMemberDialog.onAddGroupMemberDialogListener onAddGroupMemberDialogListener = new AddGroupMemberDialog.onAddGroupMemberDialogListener() {
        @Override
        public void onGroupMemberSelected(final GroupMember gm) {
            final DocumentReference recruitGroupRef = RecruitAccess.getRecruitGroupDocumentReference(
                    gm.getRecruitReference(),
                    groupUid);
            recruitGroupRef.get().addOnCompleteListener(
                    new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(
                                @NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot =
                                        task.getResult();
                                if (!snapshot.exists()) {
                                    HashMap<String, GroupMember> membersToAdd;
                                    membersToAdd = new HashMap<String, GroupMember>();
                                    membersToAdd.put(
                                            gm.getRecruitReference()
                                                    .getId(),
                                            gm);
                                    GroupAccess.updateOrAddGroupMembers(
                                            groupUid,
                                            membersToAdd);
                                    Toast.makeText(getContext(),
                                            getContext().getString(
                                                    R.string.group_member_added),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(),
                                            getContext()
                                                    .getString(
                                                            R.string.group_add_member_already_member),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            } else {
                                return;
                            }
                        }
                    });
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater
                .inflate(R.layout.fragment_group_detail, container, false);
        name = rootView.findViewById(R.id.txt_group_name);
        city = rootView.findViewById(R.id.txt_group_city);
        image = rootView.findViewById(R.id.img_group);
        listView = rootView.findViewById(R.id.list_group_members);

        groupMembers = new ArrayList<>();

        adapter = new MemberListAdapter(getContext(), groupMembers);

        listView.setAdapter(adapter);

        String uid = Properties.getInstance().getCurrentRecruit().getUid();
        DocumentReference curUserRef = GroupAccess
                .getGroupMemberDocumentReference(listener.getGroupKey(),
                        Properties.getInstance().getCurrentRecruit().getUid());
        curUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    GroupMember gm = documentSnapshot.toObject(GroupMember.class);
                    currentMember = gm;
                }
                initFab(rootView);
            }
        });

        CollectionReference membersRef = GroupAccess
                .getGroupMembersCollectionReference(listener.getGroupKey());
        membersRef.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    GroupMember groupMember = dc.getDocument().toObject(GroupMember.class);
                    switch (dc.getType()) {
                        case ADDED:
                            groupMembers.add(groupMember);
                            adapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            Recruit currentRecruit = Properties.getInstance()
                                    .getCurrentRecruit();
                            if (dc.getDocument()
                                    .getId()
                                    .equals(currentRecruit.getUid()) &&
                                    !currentRecruit.getPermissions().isAdmin()) {
                                getActivity().finish();
                            } else {
                                groupMembers.remove(groupMember);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                    }
                }
            }
        });

        String key = listener.getGroupKey();

        DocumentReference groupRef = GroupAccess.getGroupDocumentReference(listener.getGroupKey());

        groupRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Group g = documentSnapshot.toObject(Group.class);
                    g.setKey(documentSnapshot.getId());
                    groupUid = g.getKey();
                    name.setText(documentSnapshot.getId());
                    city.setText(g.getCity());
                    // TODO: init image
                }
            }
        });

        return rootView;
    }


    public void initFab(View rootView) {
        if ((currentMember != null && currentMember.getRole() == Role.LEADER) ||
                Properties.getInstance().getCurrentRecruit().getPermissions().isAdmin()) {
            fab = rootView.findViewById(R.id.fab_add_member);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AddGroupMemberDialog(getContext(), onAddGroupMemberDialogListener);
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onGroupDetailFragmentInteractionListener) {
            listener = (onGroupDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onGroupDetailFragmentInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface onGroupDetailFragmentInteractionListener {
        String getGroupKey();
    }
}
