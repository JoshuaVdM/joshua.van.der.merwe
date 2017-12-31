package com.jvdm.recruits;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Joske on 30/12/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Set local persistence
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean persistData = preferences.getBoolean(getString(R.string.pref_data_persistence_key), true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(persistData);
    }
}
