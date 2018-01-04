package com.jvdm.recruits.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.R;
import com.squareup.picasso.Picasso;

public class ProfileEditActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private DocumentReference userDocRef;
    private TextView recruitName;
    private TextView recruitEmail;
    private EditText recruitDescription;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userDocRef = RecruitAccess.getRecruitDocumentReference(currentUser.getUid());

        recruitName = findViewById(R.id.text_recruit_name);
        recruitEmail = findViewById(R.id.text_recruit_email);
        recruitDescription = findViewById(R.id.edit_profile_description);
        profilePicture = findViewById(R.id.image_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setViewValues(Recruit r) {
        recruitName.setText(r.getUsername());
        recruitEmail.setText(r.getEmail());
        recruitDescription.setText(r.getDescription());
        if (r.getPhotoUri() != null) {
            Picasso.with(this)
                    .load(Uri.parse(r.getPhotoUri()))
                    .transform(new CircleTransform())
                    .into(profilePicture);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        userDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Recruit r = documentSnapshot.toObject(Recruit.class);
                    setViewValues(r);
                }
            }
        });
    }

    public void btnSaveClick(View view) {
        userDocRef.update("description", recruitDescription.getText().toString());
        finish();
    }
}
