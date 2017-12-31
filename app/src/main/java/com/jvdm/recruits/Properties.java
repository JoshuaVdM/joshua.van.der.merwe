package com.jvdm.recruits;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jvdm.recruits.Model.Recruit;

/**
 * Created by Joske on 30/12/17.
 */

public class Properties {
    private static Properties instance = null;

    private boolean dataPersistenceChanged = false;
    private Recruit currentRecruit = null;

    protected Properties() {
    }

    public static synchronized Properties getInstance() {
        if (instance == null) {
            instance = new Properties();
        }
        return instance;
    }

    public boolean isDataPersistenceChanged() {
        return dataPersistenceChanged;
    }

    public void setDataPersistenceChanged(boolean dataPersistenceChanged) {
        this.dataPersistenceChanged = dataPersistenceChanged;
    }

    public Recruit getCurrentRecruit() {
        return currentRecruit;
    }

    public void setCurrentRecruit(Recruit currentRecruit) {
        this.currentRecruit = currentRecruit;
    }
    public void addRecruitListener(DatabaseReference reference) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recruit recruit = dataSnapshot.getValue(Recruit.class);
                if (recruit != null) {
                    currentRecruit = recruit;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}
