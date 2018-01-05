package com.jvdm.recruits;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jvdm.recruits.Model.Permission;
import com.jvdm.recruits.Model.Recruit;

import java.util.HashMap;
import java.util.Map;

public class Properties {
    private static Properties instance = null;

    private boolean dataPersistenceChanged = false;
    private Recruit currentRecruit = null;
    private DocumentReference currentRecruitRef = null;
    private HashMap<String, onPropertiesInteractionListener> listeners;

    private Properties() {
    }

    public static synchronized Properties getInstance() {
        if (instance == null) {
            instance = new Properties();
            instance.listeners = new HashMap<>();
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
                    Recruit tmp = documentSnapshot.toObject(Recruit.class);
                    Permission currPermission = currentRecruit.getPermissions();
                    currentRecruit = tmp;
                    if (tmp.getPermissions().isAdmin()
                            != currPermission.isAdmin()) {
                        callOnRecruitPermissionChanged();
                    }

                    if (!currentRecruit.getVerified()) {
                        callOnRecruitUnverified();
                    }
                    tmp.setUid(documentSnapshot.getId());
                } else {
                    callOnRecruitRemoved();
                }
            }
        });
    }

    private void callOnRecruitRemoved() {
        for (Map.Entry<String, onPropertiesInteractionListener> listener : listeners.entrySet()) {
            listener.getValue().onRecruitRemoved();
        }
    }

    private void callOnRecruitUnverified() {
        for (Map.Entry<String, onPropertiesInteractionListener> listener : listeners.entrySet()) {
            listener.getValue().onRecruitUnverified();
        }
    }

    private void callOnRecruitPermissionChanged() {
        for (Map.Entry<String, onPropertiesInteractionListener> listener : listeners.entrySet()) {
            listener.getValue().onRecruitPermissionsChanged();
        }
    }

    public void addRecruitListener(String tag, onPropertiesInteractionListener listener) {
        listener.onRecruitPermissionsChanged();
        listeners.put(tag, listener);
    }

    public void removeRecruitListener(String tag) {
        listeners.remove(tag);
    }

    public interface onPropertiesInteractionListener {
        void onRecruitRemoved();

        void onRecruitUnverified();

        void onRecruitPermissionsChanged();
    }
}

