package com.palomino.mrbp.dares;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private Profile mUser;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private ImageButton mImageButton;
    private CommentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String username = intent.getExtras().getString("username");


        ///////////////////////SET PROFILE TO PROFILE FROM DATABASE/////////////////////////////////
        mUser = new Profile(1, 5, username);
//        mUser.setProfileName(getIntent().getStringExtra(EXTRA_PROFILE_NAME, "JOE"));

        mRecyclerView = (RecyclerView)
                findViewById(R.id.comment_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEditText = (EditText)
                findViewById(R.id.comment_text);

        //////////////////ADD TO DATABASE IF THEY CLICK THIS BUTTON////////////////////////////////
        mImageButton = (ImageButton)
                findViewById(R.id.comment_send_button);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = mEditText.getText().toString();
                if(!comment.isEmpty()){
                    Comment myComment = new Comment(comment, mUser.getProfileName());
                    mAdapter.addComment(myComment);
                    mEditText.setText("");
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////
        upDateUI();
    }


    ///////////////////////ADD COMMENTS FROM DATABASE TO LIST HERE/////////////////////////////////
    private void upDateUI(){
        CommentList commentList = CommentList.get(this);
        List<Comment> comments = commentList.getComments();
        mAdapter = new CommentAdapter(comments);
        mRecyclerView.setAdapter(mAdapter);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////



    private class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {
        private List<Comment> mComments;
        public CommentAdapter(List<Comment> comments) {
            mComments = comments;
        }

        @Override
        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(CommentActivity.this);
            View view = layoutInflater
                    .inflate(R.layout.text_view_comment, parent, false);

            return new CommentHolder(view);
        }
        @Override
        public void onBindViewHolder(CommentHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.bindComment(comment);
        }
        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void addComment(Comment comment){
            mComments.add(0,comment);
            System.out.println(comment.getComment());
            System.out.println(comment.getProfileName());
            notifyItemInserted(0);
        }
    }



    private class CommentHolder extends RecyclerView.ViewHolder{
        private Comment mComment;
        public TextView mProfileTextView;
        public TextView mCommentTextView;

        public CommentHolder(View itemView){
            super(itemView);

            mProfileTextView = (TextView)
                    itemView.findViewById(R.id.comment_profile_name);
            mCommentTextView = (TextView)
                    itemView.findViewById(R.id.actual_comment);
        }

        public void bindComment(Comment comment){
            mComment = comment;
            mProfileTextView.setText(mComment.getProfileName());
            mCommentTextView.setText(mComment.getComment());
        }

    }

}
