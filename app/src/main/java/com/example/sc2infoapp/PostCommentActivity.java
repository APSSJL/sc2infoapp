package com.example.sc2infoapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.parse.ParseException;
import com.parse.ParseQuery;
import android.os.Build;

import models.Comment;
import models.Post;

public class PostCommentActivity extends BaseCommentActivity {
    TextView tvCommentPost;

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_post_comment);
        super.onCreate(savedInstanceState);

        tvCommentPost = findViewById(R.id.tvCommentPost);
        sourceId = getIntent().getStringExtra("Post");
        tvCommentPost.setText(sourceId);
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
