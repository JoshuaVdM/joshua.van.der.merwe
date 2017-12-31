package com.jvdm.recruits.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joske on 23/12/17.
 */

public class Group {
    private String name;
    private HashMap<String, Role> recruits;

    // Constructors
    public Group() {
        recruits = new HashMap<>();
    }

    public Group(String name) {
        this.name = name;
        recruits = new HashMap<>();
    }

    public Group(String name, HashMap<String, Role> recruits) {
        this.name = name;
        this.recruits = recruits;
    }

    // Getters and setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Role> getRecruits() {
        return recruits;
    }

    public void setRecruits(HashMap<String, Role> recruits) {
        this.recruits = recruits;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("recruits", recruits);
        return result;
    }

    public enum Role {
        MEMBER,
        LEADER
    }
}
