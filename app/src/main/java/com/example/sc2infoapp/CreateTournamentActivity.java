package com.example.sc2infoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import models.Tournament;
import models.UserTournament;

public class CreateTournamentActivity extends AppCompatActivity {
    public static final String TAG = "CREATE_TOURN_ACTIVITY";

    EditText etTournName;
    EditText etTournDescription;
    Button btnPostTournLogo;
    Button btnCreateTourn;

    ParseUser user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        user = ParseUser.getCurrentUser();

        //find view by id
        btnPostTournLogo = findViewById(R.id.btnPostTournLogo);
        btnCreateTourn = findViewById(R.id.btnCreateTourn);
        etTournName = findViewById(R.id.etTournName);
        etTournDescription = findViewById(R.id.etTournDescription);

        //set OnClickListener
        btnCreateTourn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tournName = etTournName.getText().toString();
                String tournDescription = etTournDescription.getText().toString();

                if (tournName.isEmpty()){
                    Toast.makeText(CreateTournamentActivity.this, "Tournament Name cannot be empty!", Toast.LENGTH_SHORT).show();

                }
                Log.i(TAG,tournName);
                Log.i(TAG,tournDescription);

                UserTournament tournament = new UserTournament();
                tournament.setOrganizer(user);
                tournament.setTournName(tournName);
                tournament.setTournDescription(tournDescription);
                tournament.setIsTeam(false);

                tournament.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while creating", e);
                            Toast.makeText(CreateTournamentActivity.this, "Error while creating", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "Created successfully");
                        finish();
                    }
                });

            }
        });



    }



}
