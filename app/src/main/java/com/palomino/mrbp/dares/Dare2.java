package com.palomino.mrbp.dares;

/**
 * Created by PERU on 12/11/2015.
 */
public class Dare2 {
    private String mDareTitle;
    private String mDareDescription;
    private int mPoints;

    private Dare2(){}

    Dare2(String title, String description, int Points){
        mDareTitle = title;
        mDareDescription = description;
        mPoints = Points;
    }

    public String getDareTitle() {
        return mDareTitle;
    }

    public void setDareTitle(String dareTitle) {
        mDareTitle = dareTitle;
    }

    public String getDareDescription() {
        return mDareDescription;
    }

    public void setDareDescription(String dareDescription) {
        mDareDescription = dareDescription;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }
}