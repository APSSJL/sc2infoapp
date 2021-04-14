package com.example.sc2infoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jsoup.Connection;

import models.Comment;

public class PlayerCommentActivity extends BaseCommentActivity {


    TextView tvPlayerName;
    TextView tvRace;
    TextView tvRating;
    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player_comment);
        layoutId =R.layout.activity_player_comment;
        sourceId = getIntent().getStringExtra("id");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_player_comment);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvRace = findViewById(R.id.tvPlayerRace);
        tvRating = findViewById(R.id.tvPlayerRating);

        tvPlayerName.setText(getIntent().getStringExtra("name"));
        tvRace.setText(getIntent().getStringExtra("race"));
        tvRating.setText(getIntent().getStringExtra("rating"));
    }

    @Override
    protected void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include("author");
        query.setLimit(5);
        query.whereEqualTo(Comment.KEY_COMMENT_TO, sourceId);
        query.addDescendingOrder("createdAt");


        try {
            allComments.addAll(query.find());
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        //Log.i(TAG, allComments.get(1).getContent());
        adapter.notifyDataSetChanged();
    }

}