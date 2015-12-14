package com.palomino.mrbp.dares;

/**
 * Created by Mr.bp on 12/11/15.
 */
public class Dare {
    public int profileDrawableId;   //profile picture
    public String profileName;      //profile name

    public Dare(int profileDrawableId, String profileName) {
        this.profileDrawableId = profileDrawableId;
        this.profileName = profileName;
    }

    public int getProfileDrawableId() {
        return profileDrawableId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileDrawableId(int profileDrawableId) {
        this.profileDrawableId = profileDrawableId;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
