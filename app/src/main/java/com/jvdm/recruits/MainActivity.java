package com.jvdm.recruits;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.Activities.ProfileEditActivity;
import com.jvdm.recruits.Activities.SettingsActivity;
import com.jvdm.recruits.DataAccess.RecruitAccess;
import com.jvdm.recruits.Fragments.GroupsFragment;
import com.jvdm.recruits.Fragments.ProfileFragment;
import com.jvdm.recruits.Fragments.RecruitVerificationFragment;
import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.Model.Recruit;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String MAINACTIVITY_RECRUIT_CHANGE = "RECRUIT_STATUS_CHANGED";

    public FirebaseAuth auth;
    public FirebaseUser currentUser;
    public FirebaseFirestore firestore;
    public NavigationView navigationView;

    private Context context;
    private TextView recruitName;
    private TextView recruitEmail;
    private ImageView profilePicture;
    private Properties.onPropertiesInteractionListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        // Initialise auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            signOut();
        }

        // Initialise database
        firestore = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize drawer toggle
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize listener
        listener = new Properties.onPropertiesInteractionListener() {
            @Override
            public void onRecruitRemoved() {

            }

            @Override
            public void onRecruitUnverified() {

            }

            @Override
            public void onRecruitPermissionsChanged() {
                initNavMenu();
                displaySelectedFragment(R.id.nav_groups);
            }
        };

        // Initialize drawer navigation
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedFragment(R.id.nav_groups);

        // Set recruit info fields listeners
        recruitName = navigationView.getHeaderView(0)
                .findViewById(R.id.text_recruit_name);
        recruitEmail = navigationView.getHeaderView(0)
                .findViewById(R.id.text_recruit_email);
        profilePicture = navigationView.getHeaderView(0)
                .findViewById(R.id.image_profile);

        DocumentReference userDocRef = RecruitAccess.getRecruitDocumentReference(
                currentUser.getUid());
        userDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    Recruit r = documentSnapshot.toObject(Recruit.class);
                    recruitName.setText(r.getUsername());
                    recruitEmail.setText(currentUser.getUid());
                    if (r.getPhotoUri() != null) {
                        Picasso.with(context).load(Uri.parse(r.getPhotoUri())).transform(
                                new CircleTransform()).into(profilePicture);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Properties.getInstance().addRecruitListener(MAINACTIVITY_RECRUIT_CHANGE, listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Properties.getInstance().removeRecruitListener(MAINACTIVITY_RECRUIT_CHANGE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_edit:
                Intent editIntent = new Intent(this, ProfileEditActivity.class);
                startActivity(editIntent);
                break;
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedFragment(id);
        return true;
    }

    public void displaySelectedFragment(int id) {
        Fragment fragment = null;
        Bundle args = null;
        switch (id) {
            case R.id.nav_groups:
                args = new Bundle();
                args.putBoolean("admin", false);

                fragment = new GroupsFragment();
                fragment.setArguments(args);
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_sign_out:
                onSignOut();
                break;
            case R.id.nav_verifications:
                fragment = new RecruitVerificationFragment();
                break;
            case R.id.nav_groups_all:
                args = new Bundle();
                args.putBoolean("admin", true);

                fragment = new GroupsFragment();
                fragment.setArguments(args);
                break;
        }
        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.layout_main, fragment)
                    .commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void initNavMenu() {
        navigationView.getMenu()
                .clear();
        if (Properties.getInstance().getCurrentRecruit().getPermissions().isAdmin()) {
            navigationView.inflateMenu(R.menu.activity_main_drawer_admin);
        } else {
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }
    }

    public void onSignOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        signOut();
                    }
                });
    }

    private void signOut() {
        finish();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
