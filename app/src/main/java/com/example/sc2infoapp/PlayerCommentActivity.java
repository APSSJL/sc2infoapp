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
import android.view.View;
import android.widget.TextView;

import org.jsoup.Connection;

public class PlayerCommentActivity extends BaseCommentActivity {


    TextView tvPlayerName;
    TextView tvRace;
    TextView tvRating;
    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player_comment);
        super.onCreate(savedInstanceState);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvRace = findViewById(R.id.tvPlayerRace);
        tvRating = findViewById(R.id.tvPlayerRating);

        tvPlayerName.setText(getIntent().getStringExtra("name"));
        tvRace.setText(getIntent().getStringExtra("race"));
        tvRating.setText(getIntent().getStringExtra("rating"));
    }

}