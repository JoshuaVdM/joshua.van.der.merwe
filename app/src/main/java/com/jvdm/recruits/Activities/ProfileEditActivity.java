package com.jvdm.recruits.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.Model.Recruit;
import com.jvdm.recruits.R;
import com.squareup.picasso.Picasso;

public class ProfileEditActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private DatabaseReference database;
    private TextView recruitName;
    private TextView recruitEmail;
    private EditText recruitDescription;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        recruitName = (TextView) findViewById(R.id.text_recruit_name);
        recruitEmail = (TextView) findViewById(R.id.text_recruit_email);
        recruitDescription = (EditText) findViewById(R.id.edit_profile_description);
        profilePicture = (ImageView) findViewById(R.id.image_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onStart() {
        super.onStart();

        Picasso.with(this).load(currentUser.getPhotoUrl()).transform(new CircleTransform()).into(profilePicture);

        final DatabaseReference ref = database.child("recruits").child(currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recruit r = dataSnapshot.getValue(Recruit.class);
                if (r != null) {
                    recruitName.setText(r.getUsername());
                    recruitEmail.setText(r.getEmail());
                    recruitDescription.setText(r.getDescription());
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void btnSaveClick(View view) {
        database.child("recruits").child(currentUser.getUid()).child("description").setValue(recruitDescription.getText().toString());
        finish();
    }
}
