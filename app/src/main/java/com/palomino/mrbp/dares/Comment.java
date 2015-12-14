package com.palomino.mrbp.dares;


/**
 * Created by PERU on 12/14/2015.
 */
public class Comment {
    String mProfileName;
    String mComment;

    public Comment(String comment, String profileName) {
        mComment = comment;
        mProfileName = profileName;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getProfileName() {
        return mProfileName;
    }

    public void setProfileName(String profileName) {
        mProfileName = profileName;
    }
}
