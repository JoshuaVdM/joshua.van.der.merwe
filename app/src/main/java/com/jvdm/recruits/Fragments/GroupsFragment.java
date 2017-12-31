package com.jvdm.recruits.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.jvdm.recruits.MainActivity;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joske on 29/12/17.
 */

public class GroupsFragment extends Fragment {
    private View rootView;
    private ListView listView;
    private MainActivity mainActivity;
    private Map<String, Group> groups;
    private List<String> values;
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("My groups");
        listView = rootView.findViewById(R.id.list_groups);

        if (values == null) {
            values = new ArrayList<>();
        }

        listenForGroups();

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
    }

    private void listenForGroups() {
        if (groups == null) {
            groups = new HashMap<>();
        }
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {
                    groups.put(dataSnapshot.getKey(), group);
                    updateValues();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {
                    groups.put(dataSnapshot.getKey(), group);
                    updateValues();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                groups.remove(dataSnapshot.getKey());
                updateValues();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mainActivity.database.child("recruit-groups").child(mainActivity.currentUser.getUid()).addChildEventListener(listener);
    }

    private void updateValues() {
        values.clear();
        for (Group group : groups.values()) {
            values.add(group.getName());
        }
        adapter.notifyDataSetChanged();
    }

}
