package com.jvdm.recruits.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joske on 23/12/17.
 */

@IgnoreExtraProperties
public class Recruit {
    private String username;
    private String email;

    private List<String> hobbies;

    // Constructors
    public Recruit() {
    }

    public Recruit(String username, String email) {
        this.username = username;
        this.email = email;
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

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    // Methods
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("email", email);
        result.put("hobbies", hobbies);

        return result;
    }
}
