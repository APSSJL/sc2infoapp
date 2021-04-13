package com.example.sc2infoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import models.Post;

import org.parceler.Parcels;

public class PostDetailActivity extends AppCompatActivity {
    public static final String TAG = "POST_DETAIL_ACTIVITY";

    Post post;
    TextView tvPostTitle;
    TextView tvPostContent;
    TextView tvPostCategory;
    TextView tvPostAuthor;
    Button btnCommentPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        btnCommentPost = findViewById(R.id.btnCommentPost);
        tvPostAuthor = findViewById(R.id.tvPostAuthor);
        tvPostCategory = findViewById(R.id.tvPostCategory);
        tvPostContent = findViewById(R.id.tvPostContent);
        tvPostTitle = findViewById(R.id.tvPostTitle);

        post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        tvPostAuthor.setText(post.getAuthor());
        tvPostCategory.setText(post.getCategory());
        tvPostContent.setText(post.getContent());
        tvPostTitle.setText(post.getTitle());

        btnCommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PostDetailActivity.this,PostCommentActivity.class);
                i.putExtra("Post",post.getTitle());
                startActivity(i);
            }
        });


    }
}
