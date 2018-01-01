package com.jvdm.recruits.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.MainActivity;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        mainActivity = (MainActivity) getActivity();
        listView = rootView.findViewById(R.id.list_groups);

        if (values == null) {
            values = new ArrayList<>();
        }

        CollectionReference groupsRef = RecruitAccess.getRecruitGroupsCollectionReference(mainActivity.currentUser.getUid());
        groupsRef.addSnapshotListener(mainActivity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    DocumentReference groupRef = dc.getDocument().getReference();
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

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity.setTitle("My groups");
    }
}
