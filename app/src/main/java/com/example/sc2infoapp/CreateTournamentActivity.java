package com.example.sc2infoapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseUser;

public class CreateTournamentActivity extends AppCompatActivity {
    public static final String TAG = "CREATE_TOURN_ACTIVITY";

    EditText etTournName;
    EditText etTournDescription;
    Button btnPostTournLogo;

    ParseUser user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        user = ParseUser.getCurrentUser();



    }



}
