package com.jvdm.recruits.DataAccess;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Joske on 23/12/17.
 */

public class DataAccess {
    protected static FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    protected static FirebaseUser getCurrentUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    protected static FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }
}
