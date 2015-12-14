package com.palomino.mrbp.dares;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PERU on 12/14/2015.
 */
public class CommentList {
    private static CommentList sCommentList;
    private List<Comment> mComments;

    public static CommentList get(Context context){
        if(sCommentList == null){
            sCommentList = new CommentList(context);
        }
        return sCommentList;
    }

    private CommentList(Context context){
        mComments = new ArrayList<>();
    }

    public List<Comment> getComments(){ return mComments; }
}