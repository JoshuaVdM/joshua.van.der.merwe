package com.jvdm.recruits;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jvdm.recruits.Activities.ProfileEditActivity;
import com.jvdm.recruits.Activities.SettingsActivity;
import com.jvdm.recruits.Fragments.GroupsFragment;
import com.jvdm.recruits.Fragments.ProfileFragment;
import com.jvdm.recruits.Helpers.CircleTransform;
import com.jvdm.recruits.Model.Recruit;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public FirebaseAuth auth;
    public FirebaseUser currentUser;
    public DatabaseReference database;
    public NavigationView navigationView;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Initialise auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance().getReference();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        profilePicture = navigationView.getHeaderView(0).findViewById(R.id.image_profile);

        if (currentUser.getPhotoUrl() != null) {
            Picasso.with(this).load(currentUser.getPhotoUrl()).transform(new CircleTransform()).into(profilePicture);
        }

        displaySelectedFragment(R.id.nav_groups);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            signOut();
        }

        navigationView.getMenu().clear();
        if (Properties.getInstance().getCurrentRecruit().getPermissions().isAdmin()) {
            navigationView.inflateMenu(R.menu.activity_main_drawer_admin);
        }
        else {
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }

        final TextView recruitname = navigationView.getHeaderView(0).findViewById(R.id.text_recruit_name);
        final TextView recruitemail = navigationView.getHeaderView(0).findViewById(R.id.text_recruit_email);

        final DatabaseReference ref = database.child("recruits").child(currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Recruit r = dataSnapshot.getValue(Recruit.class);
                    if (r != null) {
                        recruitname.setText(r.getUsername());
                        recruitemail.setText(currentUser.getUid());
                        ref.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        switch (id) {
            case R.id.nav_groups:
                fragment = new GroupsFragment();
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                break;
            case R.id.nav_sign_out:
                onSignOut();
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
