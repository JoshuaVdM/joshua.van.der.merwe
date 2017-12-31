package com.jvdm.recruits;

/**
 * Created by Joske on 30/12/17.
 */

public class Properties {
    private static Properties instance = null;

    private boolean dataPersistenceChanged = false;

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
}
