package com.jvdm.recruits.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.jvdm.recruits.Adapters.RecruitVerificationAdapter;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.MainActivity;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.Model.RecruitItem;
import com.jvdm.recruits.R;

import java.util.ArrayList;

/**
 * Created by Joske on 31/12/17.
 */

public class RecruitVerificationFragment extends ListFragment {
    private ArrayList<RecruitItem> recruitList;
    private View rootView;
    private MainActivity mainActivity;
    private RecruitVerificationAdapter adapter;
    private ListView listView;

    /*@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recruit_verification,
                container,
                false);
        mainActivity = (MainActivity) getActivity();
        listView = rootView.findViewById(android.R.id.list);

        if (recruitList == null) {
            recruitList = new ArrayList<RecruitItem>();
        }

        adapter = new RecruitVerificationAdapter(mainActivity, recruitList);
        listView.setAdapter(adapter);

        final CollectionReference recruitsRef = RecruitAccess.getRecruitsCollectionReference();
        recruitsRef.addSnapshotListener(mainActivity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    Recruit newRecruit = dc.getDocument().toObject(Recruit.class);
                    String newKey = dc.getDocument().getId();
                    switch (dc.getType()) {
                        case ADDED:
                            if (!newRecruit.getVerified()) {
                                RecruitItem item = new RecruitItem(newKey, newRecruit);
                                recruitList.add(item);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case MODIFIED:
                            RecruitItem newItem = new RecruitItem(newKey, newRecruit);
                            boolean addRecruitToList = true;
                            for (int i = 0; i < recruitList.size(); i++) {
                                RecruitItem tmp = recruitList.get(i);
                                if (tmp.getUid().equals(newItem.getUid())) {
                                    addRecruitToList = false;
                                    if (newRecruit.getVerified()) {
                                        recruitList.remove(i);
                                    } else {
                                        recruitList.set(i, newItem);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            if (addRecruitToList) {
                                recruitList.add(newItem);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case REMOVED:
                            for (int i = 0; i < recruitList.size(); i++) {
                                RecruitItem tmp = recruitList.get(i);
                                if (tmp.getUid().equals(newKey)) {
                                    recruitList.remove(i);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            break;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity.setTitle("Account verifications");
    }
}
