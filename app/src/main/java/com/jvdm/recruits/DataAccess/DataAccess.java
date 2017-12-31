package com.jvdm.recruits.DataAccess;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Joske on 23/12/17.
 */

public class DataAccess {
    protected static FirebaseAuth auth;
    protected static FirebaseUser currentUser;
    protected static DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    protected static void updateAuth() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }
}
