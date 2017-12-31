package com.jvdm.recruits.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joske on 23/12/17.
 */

@IgnoreExtraProperties
public class Recruit implements FirebaseDatabaseModel {
    private String username;
    private String email;
    private String description;
    private Permission permissions;
    private Boolean verified;

    // Constructors
    public Recruit() {
    }

    public Recruit(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Recruit(String username, String email, String description, Boolean verified) {
        this.username = username;
        this.email = email;
        this.description = description;
        this.verified = verified;
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Permission getPermissions() {
        return permissions;
    }

    public void setPermissions(Permission permissions) {
        this.permissions = permissions;
    }

    // Methods
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("email", email);
        result.put("description", description);
        result.put("verified", verified);
        result.put("permissions", permissions);

        return result;
    }
}