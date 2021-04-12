package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import adapters.BaseCommentAdapter;
import models.Comment;

public class MatchCommentActivity extends BaseCommentActivity {

    TextView tvMatchCommentTitle;

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_match_comment);
        super.onCreate(savedInstanceState);

        tvMatchCommentTitle = findViewById(R.id.tvMatchCommentTitle);
        sourceId = String.format("%s, %s, %d", getIntent().getStringExtra("opponent"), getIntent().getStringExtra("time"), getIntent().getIntExtra("type", 0));
        tvMatchCommentTitle.setText(sourceId);
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