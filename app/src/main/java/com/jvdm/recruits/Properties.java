package com.jvdm.recruits;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.Model.Recruit;

public class Properties {
    private static Properties instance = null;

    private boolean dataPersistenceChanged = false;
    private Recruit currentRecruit = null;
    private DocumentReference currentRecruitRef = null;

    private Properties() {
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

    void setCurrentRecruit(Recruit currentRecruit) {
        this.currentRecruit = currentRecruit;
    }

    public DocumentReference getCurrentRecruitRef() {
        return currentRecruitRef;
    }

    public void setCurrentRecruitRef(DocumentReference currentRecruitRef) {
        this.currentRecruitRef = currentRecruitRef;
    }

    void listenForRecruit(DocumentReference reference) {
        currentRecruitRef = reference;
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    currentRecruit = documentSnapshot.toObject(Recruit.class);
                    currentRecruit.setUid(documentSnapshot.getId());
                }
            }
        });
    }
}
