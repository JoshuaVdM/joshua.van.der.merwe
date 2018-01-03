package com.jvdm.recruits.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.DataAccess.RecruitAccess;
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
    private List<String> values;
    private ArrayAdapter adapter;
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
        setFabAction();

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

        return rootView;
    }

    private void setFabAction() {
        if (admin) {
            fab = mainActivity.findViewById(R.id.fab);

            fab.setImageResource(R.drawable.ic_add_white);

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
                        String name = dc.getDocument().getId();
                        switch (dc.getType()) {
                            case ADDED:
                                values.add(name);
                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                values.remove(name);
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
                        String name = dc.getDocument().getId();
                        Group g = dc.getDocument().toObject(Group.class);
                        switch (dc.getType()) {
                            case ADDED:
                                values.add(name);
                                adapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                values.remove(name);
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
