package com.jvdm.recruits;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Model.Recruit;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    // Request code
    private static final int RC_SIGN_IN = 123;

    // Login providers
    private List<AuthUI.IdpConfig> providers;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference database;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();


        setContentView(R.layout.activity_login);
        // Choose authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                // new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
        // Create and launch sign-in intent
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
    }

    private void checkLogin() {
        refreshAuth();
        if (currentUser == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        } else {
            onLoggedIn();
        }
    }

    public void refreshAuth() {
        currentUser = mAuth.getCurrentUser();
    }

    public void onVerified(Recruit r) {

        Properties properties = Properties.getInstance();
        properties.setCurrentRecruit(r);
        properties.addRecruitListener(database.child("recruits").child(currentUser.getUid()));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public Recruit fixRecruit(Recruit r) {
        if (TextUtils.isEmpty(r.getPhotoUri()) && currentUser.getPhotoUrl() != null) {
            r.setPhotoUri(currentUser.getPhotoUrl().toString());
        }
        database.child("recruits").child(currentUser.getUid()).setValue(r);
        return r;
    }

    public void onNotVerified() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_not_verified))
                .setTitle(getString(R.string.title_not_verified));

        builder.setPositiveButton(getString(R.string.action_choose_account), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSignOut();
            }
        });

        builder.setNegativeButton(getString(R.string.action_exit_app), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                AuthUI.getInstance()
                        .signOut(getApplicationContext());
                finish();
                moveTaskToBack(true);
            }
        });

        builder.create().show();
    }

    public void onLoggedIn() {
        ValueEventListener recruitListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recruit r = dataSnapshot.getValue(Recruit.class);
                if (r != null) {
                    r = fixRecruit(r);
                    if (r.getVerified() != null) {
                        if (r.getVerified()) {
                            onVerified(r);
                        }
                        else {
                            onNotVerified();
                        }
                    }
                }
                else {
                    RecruitAccess.add(currentUser, database);
                    onLoggedIn();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        updateUserRef();
        DatabaseReference user = database.child("recruits").child(currentUser.getUid());
        user.addListenerForSingleValueEvent(recruitListener);
    }

    public void updateUserRef() {
        if (userRef != null) {
            userRef.keepSynced(false);
        }
        userRef = database.child("recruits").child(currentUser.getUid());
        userRef.keepSynced(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                refreshAuth();
                onLoggedIn();
                // ...
            }
        }
    }

    public void onSignOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        checkLogin();
                    }
                });
    }
}
