package com.jvdm.recruits.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.jvdm.recruits.Adapters.RecruitVerificationAdapter;
import com.jvdm.recruits.MainActivity;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.Model.RecruitItem;
import com.jvdm.recruits.R;

import java.util.ArrayList;

/**
 * Created by Joske on 31/12/17.
 */

public class RecruitVerificationFragment extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recruit_verification, container, false);
        mainActivity = (MainActivity) getActivity();
        listView = rootView.findViewById(R.id.list_recruit_verifications);

        if (recruitList == null) {
            recruitList = new ArrayList<RecruitItem>();
        }

        adapter = new RecruitVerificationAdapter(mainActivity, recruitList);
        listView.setAdapter(adapter);


        DatabaseReference ref = mainActivity.database.child("recruits");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Recruit r = dataSnapshot.getValue(Recruit.class);
                if (!r.getVerified()) {
                    RecruitItem item = new RecruitItem(dataSnapshot.getKey(), r);
                    recruitList.add(item);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Recruit r = dataSnapshot.getValue(Recruit.class);
                RecruitItem newItem = new RecruitItem(dataSnapshot.getKey(), r);
                RecruitItem oldItem = null;
                int index = -1;
                for (int i = 0; i < recruitList.size(); i++) {
                    RecruitItem tmp = recruitList.get(i);
                    if (tmp.getUid() == newItem.getUid()) {
                        oldItem = tmp;
                        index = i;
                    }
                }
                if (index != -1) {
                    if (r.getVerified()) {
                        recruitList.remove(index);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Recruit r = dataSnapshot.getValue(Recruit.class);

                recruitList.remove(new RecruitItem(dataSnapshot.getKey(), r));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
