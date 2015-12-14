package com.palomino.mrbp.dares;

/**
 * Created by Mr.bp on 12/11/15.
 */
public class TextDare extends Dare {
    public String postDescription;     //Post info

    public TextDare(int profileDrawableId, String profileName, String post) {
        super(profileDrawableId, profileName);
        this.postDescription = post;
    }
}
