package com.jvdm.recruits;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Created by Joske on 30/12/17.
 */

public class MyApplication extends Application {
    private static final String APPLICATION_TAG = "APPLICATION_TAG";
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        Properties.onPropertiesInteractionListener listener;
        listener = new Properties.onPropertiesInteractionListener() {
            @Override
            public void onRecruitRemoved() {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(context, "Your account was removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecruitUnverified() {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(
                        context,
                        "Your account has been unverified",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecruitPermissionsChanged() {

            }
        };
        Properties.getInstance().addRecruitListener(APPLICATION_TAG, listener);

        // Set local persistence
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean persistData = preferences.getBoolean(getString(R.string.pref_data_persistence_key),
                true);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(persistData)
                .build();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.setFirestoreSettings(settings);
    }
}
