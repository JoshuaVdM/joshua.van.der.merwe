package com.jvdm.recruits.Fragments;

import android.net.Uri;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.DataAccess.RecruitAccess;
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
    private TextView recruitInfo;
    private ImageView profilePicture;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle(getResources().getString(R.string.profile_title));

        setHasOptionsMenu(true);

        // Initialize views
        recruitName = rootView.findViewById(R.id.text_recruit_name);
        recruitEmail = rootView.findViewById(R.id.text_recruit_email);
        recruitInfo = rootView.findViewById(R.id.text_recruit_info);
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

        DocumentReference userDocRef;
        userDocRef = RecruitAccess.getRecruitDocumentReference(mainActivity.currentUser.getUid());
        userDocRef.addSnapshotListener(mainActivity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Recruit r = documentSnapshot.toObject(Recruit.class);
                    recruitName.setText(r.getUsername());
                    recruitEmail.setText(r.getEmail());
                    recruitInfo.setText(r.getDescription());
                    if (r.getPhotoUri() != null) {
                        Picasso.with(mainActivity)
                                .load(Uri.parse(r.getPhotoUri()))
                                .transform(new CircleTransform())
                                .into(profilePicture);
                    }
                }
            }
        });
    }
}
