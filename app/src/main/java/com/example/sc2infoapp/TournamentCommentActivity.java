package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;

import models.Comment;

public class TournamentCommentActivity extends BaseCommentActivity {

    TextView tvTournamentName;

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_tournament_comment);
        super.onCreate(savedInstanceState);

        tvTournamentName = findViewById(R.id.tvTournamentName);
        sourceId = getIntent().getStringExtra("title");
        tvTournamentName.setText(sourceId);
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

        adapter.notifyDataSetChanged();
    }
}