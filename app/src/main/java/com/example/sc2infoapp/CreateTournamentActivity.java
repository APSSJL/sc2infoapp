package com.example.sc2infoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import models.Tournament;
import models.UserTournament;

import static com.example.sc2infoapp.UpdateProfileActivity.CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE;

public class CreateTournamentActivity extends AppCompatActivity {
    public static final String TAG = "CREATE_TOURN_ACTIVITY";

    EditText etTournName;
    EditText etTournDescription;
    Button btnPostTournLogo;
    Button btnCreateTourn;
    CheckBox cbIsTeam;
    ImageView ivTournLogo;

    private Bitmap photoFile;

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
        cbIsTeam = findViewById(R.id.cbIsTeam);

        btnPostTournLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE);
            }
        });

        //set OnClickListener
        btnCreateTourn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tournName = etTournName.getText().toString();
                String tournDescription = etTournDescription.getText().toString();
                Boolean isTeam = cbIsTeam.isChecked();




                if (tournName.isEmpty()){
                    Toast.makeText(CreateTournamentActivity.this, "Tournament Name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Log.i(TAG,tournName);
//                Log.i(TAG,tournDescription);

                UserTournament userTournament = new UserTournament();
                Tournament tournament = new Tournament();
                createTourn(userTournament, tournament, tournName, tournDescription);

                if (isTeam){
                    userTournament.setIsTeam(true);
                    Log.i(TAG,"TRUE");
                }
                else{
                    userTournament.setIsTeam(false);
                    Log.i(TAG,"False");
                }

                tournament.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while creating", e);
                            Toast.makeText(CreateTournamentActivity.this, "Error while creating", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i(TAG, "Created successfully");
                        finish();
                    }
                });

            }
        });
    }


    public void createTourn(UserTournament userTournament, Tournament tournament, String tournName, String tournDescription){

        userTournament.setOrganizer(user);
        userTournament.setTournName(tournName);
        userTournament.setTournDescription(tournDescription);

        tournament.setUserCreated(userTournament);
        tournament.setFollow();
        tournament.setTournName(tournName);
    }

    public void createMatch(){

    }


}
