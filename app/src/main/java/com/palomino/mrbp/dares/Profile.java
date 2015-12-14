package com.palomino.mrbp.dares;

/**
 * Created by PERU on 12/12/2015.
 */
public class Profile {
    int mPoints;
    int mFriends;
    String mProfileName;
    int mUserID;

    public Profile(int points, int friends, String profileName) {
        mPoints = points;
        mFriends = friends;
        mProfileName = profileName;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int userID) {
        mUserID = userID;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public int getFriends() {
        return mFriends;
    }

    public void setFriends(int friends) {
        mFriends = friends;
    }

    public String getProfileName() {
        return mProfileName;
    }

    public void setProfileName(String profileName) {
        mProfileName = profileName;
    }
}