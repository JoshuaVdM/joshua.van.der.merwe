package com.jvdm.recruits.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jvdm.recruits.Adapters.MemberListAdapter;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.Model.GroupMember;
import com.jvdm.recruits.R;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joske on 03/01/2018.
 */

public class GroupDetailFragment extends Fragment {
    private TextView name;
    private TextView city;
    private ImageView image;
    private ListView listView;
    private onGroupDetailFragmentInteractionListener listener;
    private List<GroupMember> groupMembers;
    private MemberListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_group_detail, container, false);
        name = rootView.findViewById(R.id.txt_group_name);
        city = rootView.findViewById(R.id.txt_group_city);
        image = rootView.findViewById(R.id.img_group);
        listView = rootView.findViewById(R.id.list_group_members);

        groupMembers = new ArrayList<>();

        adapter = new MemberListAdapter(getContext(), groupMembers);

        listView.setAdapter(adapter);


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
                            groupMembers.remove(groupMember);
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });

        DocumentReference groupRef = GroupAccess.getGroupDocumentReference(listener.getGroupKey());

        groupRef.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Group g = documentSnapshot.toObject(Group.class);
                    name.setText(documentSnapshot.getId());
                    city.setText(g.getCity());
                    // TODO: init image
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof onGroupDetailFragmentInteractionListener) {
            listener = (onGroupDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement onGroupDetailFragmentInteractionListener.");
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
