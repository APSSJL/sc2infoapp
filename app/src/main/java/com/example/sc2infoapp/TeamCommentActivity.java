package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

public class TeamCommentActivity extends BaseCommentActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_base_comments);
        super.onCreate(savedInstanceState);

    }
}