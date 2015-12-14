package com.palomino.mrbp.dares;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class PostDare extends AppCompatActivity {
    private boolean wroteDare = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_dare);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageButton imageButton = (ImageButton) findViewById(R.id.doneButton);
        imageButton.setColorFilter(getResources().getColor(R.color.colorAccent));


        final EditText editText = (EditText) findViewById(R.id.textContainer);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    wroteDare = false;
                    imageButton.setColorFilter(getResources().getColor(R.color.colorAccent));
                } else {
                    wroteDare = true;
                    imageButton.setColorFilter(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wroteDare) {
                    //add new post to timeline
                    String post = editText.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("Post", post);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    //do nothing
                }
            }
        });

    }

}
