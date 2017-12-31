package com.jvdm.recruits.Model;


import android.net.Uri;

/**
 * Created by Joske on 31/12/17.
 */

public class RecruitItem {
    private String uid;
    private String name;

    private Uri pictureUri;

    public RecruitItem(String uid, String name, Uri pictureUri) {
        this.uid = uid;
        this.name = name;
        this.pictureUri = pictureUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(Uri pictureUri) {
        this.pictureUri = pictureUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
