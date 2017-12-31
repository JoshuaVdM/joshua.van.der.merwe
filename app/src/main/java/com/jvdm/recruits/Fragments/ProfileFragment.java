package com.jvdm.recruits.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.MainActivity;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.R;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private View rootView;
    private MainActivity mainActivity;
    private TextView recruitName;
    private TextView recruitEmail;
    private ImageView profilePicture;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle(getResources().getString(R.string.profile_title));

        setHasOptionsMenu(true);

        // Initialize views
        recruitName = rootView.findViewById(R.id.text_recruit_name);
        recruitEmail = rootView.findViewById(R.id.text_recruit_email);
        profilePicture = rootView.findViewById(R.id.image_profile);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Load profile picture
        Picasso.with(mainActivity).load(mainActivity.currentUser.getPhotoUrl()).transform(new CircleTransform()).into(profilePicture);

        final DatabaseReference ref = mainActivity.database.child("recruits").child(mainActivity.currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recruit r = dataSnapshot.getValue(Recruit.class);
                if (r != null) {
                    recruitName.setText(r.getUsername());
                    recruitEmail.setText(r.getEmail());
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
