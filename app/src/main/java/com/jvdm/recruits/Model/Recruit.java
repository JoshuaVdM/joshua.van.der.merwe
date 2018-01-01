package com.jvdm.recruits.Model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by Joske on 23/12/17.
 */

@IgnoreExtraProperties
public class Recruit {
    private String username;
    private String email;
    private String description;
    private Permission permissions;
    private Boolean verified;
    private String photoUri;

    // Constructors
    public Recruit() {
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission permissions) {
        this.permissions = permissions;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
