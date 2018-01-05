package com.jvdm.recruits.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.Adapters.GroupListAdapter;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.Fragments.GroupDetailFragment;
import com.jvdm.recruits.Model.GroupMember;
import com.jvdm.recruits.Properties;
import com.jvdm.recruits.R;

public class GroupDetailActivity extends AppCompatActivity implements
        GroupDetailFragment.onGroupDetailFragmentInteractionListener {

    String groupKey;
    GroupMember currentGroupMember;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupKey = getIntent().getStringExtra(GroupListAdapter.GROUP_KEY_INTENT);

        GroupAccess.getGroupDocumentReference(groupKey)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            setTitle(documentSnapshot.getId());
                        } else {
                            Toast.makeText(
                                    GroupDetailActivity.this,
                                    "Group was removed",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        GroupAccess.getGroupMemberDocumentReference(
                groupKey,
                Properties.getInstance()
                        .getCurrentRecruit()
                        .getUid()
        ).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    GroupMember gm = documentSnapshot.toObject(GroupMember.class);
                    switch (gm.getState()) {
                        case ACCEPTED:
                            currentGroupMember = gm;
                            break;
                        case PENDING:
                            onNavigateUp();
                            break;
                        case DECLINED:
                            onNavigateUp();
                            break;
                    }
                } else {
                    onNavigateUp();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout
                .ViewPagerOnTabSelectedListener(mViewPager));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getGroupKey() {
        return groupKey;
    }

    @Override
    public GroupMember getCurrentGroupMember() {
        return currentGroupMember;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new GroupDetailFragment();
                    break;
                case 1:
                    fragment = new GroupDetailFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
