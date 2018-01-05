package com.jvdm.recruits.Model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Group {
    @Exclude
    private String key;
    private String city;

    // Constructors

    public Group() {
    }

    // Getters and setters
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return key.equals(group.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
