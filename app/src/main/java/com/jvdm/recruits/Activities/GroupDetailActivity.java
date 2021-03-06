package com.jvdm.recruits.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.Adapters.GroupListAdapter;
import com.jvdm.recruits.DataAccess.GroupAccess;
import com.jvdm.recruits.Dialogs.InactivateGroupDialog;
import com.jvdm.recruits.Dialogs.LeaveGroupDialog;
import com.jvdm.recruits.Fragments.GroupDetailFragment;
import com.jvdm.recruits.Model.Group;
import com.jvdm.recruits.Model.GroupMember;
import com.jvdm.recruits.Model.Permission;
import com.jvdm.recruits.Properties;
import com.jvdm.recruits.R;

public class GroupDetailActivity extends AppCompatActivity implements
        GroupDetailFragment.onGroupDetailFragmentInteractionListener {

    public static final String GROUPDETAILACTIVITY_TAG = "GROUPDETAILACTIVITY_TAG";
    String groupKey;
    Group currentGroup;
    GroupMember currentGroupMember;
    Menu menu;
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

        initGroupListeners();
        initRecruitPermissionsListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Properties.getInstance().removeRecruitListener(GROUPDETAILACTIVITY_TAG);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem leaveGroup = menu.findItem(R.id.action_leave_group);
        MenuItem inactivateGroup = menu.findItem(R.id.action_inactivate_group);
        MenuItem reactivateGroup = menu.findItem(R.id.action_reactivate_group);
        MenuItem deleteGroup = menu.findItem(R.id.action_delete_group);

        if (currentGroupMember != null) {
            leaveGroup.setVisible(true);
        } else {
            leaveGroup.setVisible(false);
        }

        boolean isAdmin = Properties.getInstance()
                .getCurrentRecruit()
                .getPermissions()
                .isAdmin();

        if (currentGroup != null) {
            if (currentGroup.isActive()) {
                inactivateGroup.setVisible(isAdmin);
                reactivateGroup.setVisible(false);
            } else {
                reactivateGroup.setVisible(isAdmin);
                inactivateGroup.setVisible(false);
            }
        }

        deleteGroup.setVisible(isAdmin);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_detail, menu);
        this.menu = menu;
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
                break;
            case R.id.action_leave_group:
                new LeaveGroupDialog(
                        this,
                        groupKey,
                        new LeaveGroupDialog.onLeaveGroupDialogListener() {
                            @Override
                            public void onGroupLeave() {
                                DocumentReference groupMemberRef;
                                groupMemberRef = GroupAccess.getGroupMemberDocumentReference(
                                        groupKey,
                                        Properties.getInstance()
                                                .getCurrentRecruit()
                                                .getUid()
                                );
                                groupMemberRef.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(
                                                        GroupDetailActivity.this,
                                                        R.string.group_member_leave_group_succesful,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(
                                                        GroupDetailActivity.this,
                                                        getResources()
                                                                .getString(
                                                                        R.string.group_member_leave_group_failure,
                                                                        groupKey),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                break;
            case R.id.action_inactivate_group:
                if (!Properties.getInstance().getCurrentRecruit().getPermissions().isAdmin()) {
                    break;
                }
                new InactivateGroupDialog(this,
                        groupKey,
                        getString(R.string.group_inactivate_title),
                        getString(R.string.group_inactivate_message, groupKey),
                        new InactivateGroupDialog.onInactivateGroupDialogListener() {
                            @Override
                            public void onGroupInactivated() {
                                DocumentReference groupRef;
                                groupRef = GroupAccess.getGroupDocumentReference(groupKey);
                                groupRef.update("active", false)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(GroupDetailActivity.this,
                                                        R.string.group_inactivate_success,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GroupDetailActivity.this,
                                                        R.string.group_inactivate_failure,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                break;
            case R.id.action_reactivate_group:
                if (!Properties.getInstance().getCurrentRecruit().getPermissions().isAdmin()) {
                    break;
                }
                new InactivateGroupDialog(this,
                        groupKey,
                        getString(R.string.group_reactivate_title),
                        getString(R.string.group_reactivate_message, groupKey),
                        new InactivateGroupDialog.onInactivateGroupDialogListener() {
                            @Override
                            public void onGroupInactivated() {
                                DocumentReference groupRef;
                                groupRef = GroupAccess.getGroupDocumentReference(groupKey);
                                groupRef.update("active", true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(GroupDetailActivity.this,
                                                        R.string.group_reactivate_success,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(GroupDetailActivity.this,
                                                        R.string.group_reactivate_failure,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                break;
            case R.id.action_delete_group:
                if (!Properties.getInstance().getCurrentRecruit().getPermissions().isAdmin()) {
                    break;
                }
                new InactivateGroupDialog(this,
                        groupKey,
                        getString(R.string.group_delete_title),
                        getString(R.string.group_delete_message, groupKey),
                        new InactivateGroupDialog.onInactivateGroupDialogListener() {
                            @Override
                            public void onGroupInactivated() {
                                DocumentReference groupRef;
                                groupRef = GroupAccess.getGroupDocumentReference(groupKey);
                                groupRef.delete();
                            }
                        });

                break;
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

    private void initRecruitPermissionsListener() {
        Properties.getInstance().addRecruitListener(GROUPDETAILACTIVITY_TAG,
                new Properties.onPropertiesInteractionListener() {
                    @Override
                    public void onRecruitRemoved() {

                    }

                    @Override
                    public void onRecruitUnverified() {

                    }

                    @Override
                    public void onRecruitPermissionsChanged(Permission newPermissions) {
                        invalidateOptionsMenu();
                        if (!newPermissions.isAdmin()) {
                            finish();
                        }
                    }
                });
    }

    private void initGroupListeners() {
        GroupAccess.getGroupDocumentReference(groupKey)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            Group g = documentSnapshot.toObject(Group.class);
                            if (!g.isActive()
                                    && !Properties.getInstance().
                                    getCurrentRecruit().
                                    getPermissions()
                                    .isAdmin()) {
                                finish();
                            }
                            currentGroup = g;
                            invalidateOptionsMenu();
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

        DocumentReference groupMemberRef = GroupAccess.getGroupMemberDocumentReference(
                groupKey,
                Properties.getInstance()
                        .getCurrentRecruit()
                        .getUid()
        );

        groupMemberRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    GroupMember gm = documentSnapshot.toObject(GroupMember.class);
                    gm.setKey(documentSnapshot.getId());
                    boolean admin;
                    admin = Properties.getInstance().getCurrentRecruit().getPermissions().isAdmin();
                    switch (gm.getState()) {
                        case ACCEPTED:
                            currentGroupMember = gm;
                            break;
                        case PENDING:
                            if (!admin) {
                                onNavigateUp();
                            }
                            break;
                        case DECLINED:
                            if (!admin) {
                                onNavigateUp();
                            }
                            break;
                    }
                } else if (!Properties.getInstance()
                        .getCurrentRecruit()
                        .getPermissions()
                        .isAdmin()) {
                    onNavigateUp();
                }
                invalidateOptionsMenu();
            }
        });
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
