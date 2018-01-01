package com.jvdm.recruits;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.Model.Recruit;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    // Request code
    private static final int RC_SIGN_IN = 123;

    // Login providers
    private List<AuthUI.IdpConfig> providers;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DocumentReference userDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();


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
        currentUser = auth.getCurrentUser();
    }

    public void onVerified(String key, Recruit r) {
        Properties properties = Properties.getInstance();
        properties.setCurrentRecruit(r);
        properties.listenForRecruit(RecruitAccess.getRecruitDocumentReference(key));

        updateGroup();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void updateGroup() {
        Group g = new Group();
        g.setCity("Brussel");

        GroupAccess.add("Recruits Brussel", g);
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
        userDocRef = RecruitAccess.getRecruitDocumentReference(currentUser.getUid());
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Recruit r = documentSnapshot.toObject(Recruit.class);
                    if (r.getVerified() != null) {
                        if (r.getVerified()) {
                            onVerified(documentSnapshot.getId(), r);
                        } else {
                            onNotVerified();
                        }
                    }
                } else {
                    RecruitAccess.add(currentUser);
                    onLoggedIn();
                }
            }
        });
    }

    // Enable firestore offline access and update
   /* public void updateUserRef() {
        if (userRef != null) {
            userRef.keepSynced(false);
        }
        userRef = database.child("recruits").child(currentUser.getUid());
        userRef.keepSynced(true);
    }*/

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
        refreshAuth();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        checkLogin();
                    }
                });
    }
}
