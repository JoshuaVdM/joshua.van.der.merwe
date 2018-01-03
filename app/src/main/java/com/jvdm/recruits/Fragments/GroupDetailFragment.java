package com.jvdm.recruits.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.R;

import java.lang.annotation.Documented;

/**
 * Created by joske on 03/01/2018.
 */

public class GroupDetailFragment extends Fragment {
    private TextView name;
    private TextView city;
    private ImageView image;
    private onGroupDetailFragmentInteractionListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_detail, container, false);
        name = rootView.findViewById(R.id.txt_group_name);
        city = rootView.findViewById(R.id.txt_group_city);
        image = rootView.findViewById(R.id.img_group);

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
