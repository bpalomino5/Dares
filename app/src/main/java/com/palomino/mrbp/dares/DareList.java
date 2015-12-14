package com.palomino.mrbp.dares;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PERU on 12/11/2015.
 */
public class DareList {
    private static DareList sDareList;
    private List<Dare2> mDares;

    public static DareList get(Context context) {
        if (sDareList == null) {
            sDareList = new DareList(context);
        }
        return sDareList;
    }

    private DareList(Context context) {
        mDares = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Dare2 dare = new Dare2("Profile" + i, "Description" + i, i);
            mDares.add(dare);
        }
    }

    public List<Dare2> getDares() {
        return mDares;
    }
}