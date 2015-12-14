package com.palomino.mrbp.dares;

/**
 * Created by Mr.bp on 12/11/15.
 */
public class VideoDare extends Dare {
    public String description;  //description of video
    //public URL                //location of video


    public VideoDare(int profileDrawableId, String profileName, String description) {
        super(profileDrawableId, profileName);
        this.description = description;
    }
}
