package com.example.sc2infoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Match;
import models.Player;
import models.Team;
import models.TeamMatch;
import models.Tournament;
import models.UserTournament;

import static com.example.sc2infoapp.UpdateProfileActivity.CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE;

public class EditTournamentActivity extends AppCompatActivity {
    public static final String TAG = "EDIT_TOURN_ACTIVITY";

    EditText etTournName;
    EditText etTournDescription;
    Button btnPostTournLogo;
    Button btnSaveTourn;
    CheckBox cbIsTeam;
    ImageView ivTournLogo;
    TextView tvIsTeam;
    TextView tvReminder;

    //Match stuff
    EditText spObj1;
    EditText spObj2;
    EditText etMatchDescription;
    Button btnCreateMatch;
    EditText etMatchDate;

    UserTournament userTournament;
    Tournament tournament;

    Match match;
    TeamMatch teamMatch;

    ParseUser user;
    private Bitmap photoFile;
    private File result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        userTournament = Parcels.unwrap(getIntent().getParcelableExtra("userTournament"));
        tournament = Parcels.unwrap(getIntent().getParcelableExtra("tournament"));

        Log.i(TAG,userTournament.getName());

        etTournName = findViewById(R.id.etTournName);
        etTournDescription = findViewById(R.id.etTournDescription);

        btnPostTournLogo = findViewById(R.id.btnPostTournLogo);
        btnSaveTourn = findViewById(R.id.btnSaveTourn);
        cbIsTeam = findViewById(R.id.cbIsTeam);
        ivTournLogo = findViewById(R.id.ivTournLogo);
        tvIsTeam = findViewById(R.id.tvIsTeam);
        tvReminder = findViewById(R.id.tvReminder);

        spObj1 = findViewById(R.id.spObj1);
        spObj2 = findViewById(R.id.spObj2);
        etMatchDescription = findViewById(R.id.etMatchDescription);
        btnCreateMatch = findViewById(R.id.btnCreateMatch);
        etMatchDate=findViewById(R.id.etMatchDate);

        cbIsTeam.setVisibility(View.GONE);

        user = ParseUser.getCurrentUser();

        tvReminder.setText("* Don't forget, you can also access tournaments from your user profile!");

        ParseQuery<UserTournament> finaluserTournamentQuery = ParseQuery.getQuery("userTournament");
        finaluserTournamentQuery.whereEqualTo("objectId",userTournament.getObjectId());
        finaluserTournamentQuery.findInBackground(new FindCallback<UserTournament>() {
            @Override
            public void done(List<UserTournament> objects, ParseException e) {
                Boolean isTeam = objects.get(0).isTeam();

                if (isTeam){tvIsTeam.setText("This is a team match tournament, type teams below to create matches!"); }
                else{tvIsTeam.setText("this is a player match tournament, type players below to create matches!");}
            }
        });

        btnPostTournLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE);
            }
        });

        btnCreateMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Parse whether team or player match tournament
                ParseQuery<UserTournament> userTournQuery = ParseQuery.getQuery("userTournament");
                userTournQuery.whereEqualTo("objectId",userTournament.getObjectId());
                userTournQuery.findInBackground(new FindCallback<UserTournament>() {
                    @Override
                    public void done(List<UserTournament> objects, ParseException e) {
                        Boolean isTeam;
                        isTeam = objects.get(0).isTeam();
                        if (isTeam){
                            Log.i("TAG","isteam");
                        }
                        //if Team Match
                        if (isTeam){
                            //Query through teams
                            ParseQuery<Team> Obj1query = ParseQuery.getQuery("Team");
                            Obj1query.whereEqualTo("teamName",spObj1.getText().toString());

                            ParseQuery<Team> Obj2query = ParseQuery.getQuery("Team");
                            Obj2query.whereEqualTo("teamName",spObj2.getText().toString());

                            List<ParseQuery<Team>> queries = new ArrayList<ParseQuery<Team>>();
                            queries.add(Obj1query);
                            queries.add(Obj2query);

                            ParseQuery<Team> mainQuery = ParseQuery.or(queries);
                            mainQuery.findInBackground(new FindCallback<Team>() {
                                @Override
                                public void done(List<Team> objects, ParseException e) {
                                    if (objects.size()<2){
                                        Toast.makeText(EditTournamentActivity.this,"Team 1 or Team 2 is non-existant",Toast.LENGTH_SHORT).show();
                                        Log.e(TAG,"Team 1 or Team 2 is non-existant",e);
                                        return;
                                    }
                                    Team team1 = objects.get(0);
                                    Team team2 = objects.get(1);
                                    Log.i(TAG,"Team1: "+team1.getTeamName());
                                    Log.i(TAG,"Team2: "+team2.getTeamName());

                                    teamMatch = new TeamMatch();
                                    teamMatch.setDetails(etMatchDescription.getText().toString());
                                    teamMatch.setTeam1(team1);
                                    teamMatch.setTeam2(team2);

                                    //Get Date
                                    Date date = new Date();
                                    date.getTime();
                                    teamMatch.setTime(date);

                                    teamMatch.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.e(TAG, "Error while creating", e);
                                                Toast.makeText(EditTournamentActivity.this, "Error while creating", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Log.i(TAG, "Created successfully");
                                            Log.i(TAG,"teamMatch ID:"+teamMatch.getObjectId());

                                            //add match to userTournament matches
                                            userTournament.add("matches",teamMatch.getObjectId());
                                            userTournament.saveInBackground();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                        //if player Match
                        else{
                            //Query through player
                            ParseQuery<ParseUser> Obj1query = ParseUser.getQuery();
                            Obj1query.whereEqualTo("username",spObj1.getText().toString());

                            ParseQuery<ParseUser> Obj2query = ParseUser.getQuery();
                            Obj2query.whereEqualTo("username",spObj2.getText().toString());

                            List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
                            queries.add(Obj1query);
                            queries.add(Obj2query);

                            ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);
                            mainQuery.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> objects, ParseException e) {
                                    if (objects.size()<2){
                                        Toast.makeText(EditTournamentActivity.this,"Player 1 or Player 2 is non-existant",Toast.LENGTH_SHORT).show();
                                        Log.e(TAG,"Player 1 or Player 2 is non-existant",e);
                                        return;
                                    }
                                    ParseUser player1 = objects.get(0);
                                    ParseUser player2 = objects.get(1);
                                    Log.i(TAG,"Team1: "+player1);
                                    Log.i(TAG,"Team2: "+player2);

                                    match = new Match();
                                    match.setDetails(etMatchDescription.getText().toString());
                                    match.setPlayer1(player1);
                                    match.setPlayer2(player2);

                                    match.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.e(TAG, "Error while creating", e);
                                                Toast.makeText(EditTournamentActivity.this, "Error while creating", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Log.i(TAG, "Created successfully");
                                            Log.i(TAG,"teamMatch ID:"+match.getObjectId());

                                            //add match to userTournament matches
                                            userTournament.add("matches",match.getObjectId());
                                            userTournament.saveInBackground();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });

        btnSaveTourn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tourName = etTournName.getText().toString();
                String tournDescription = etTournDescription.getText().toString();


                //Name cannot be empty
                if (tourName.isEmpty()){
                    Toast.makeText(EditTournamentActivity.this,"Tournament Name cannot be empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseQuery<UserTournament> userTournamentParseQuery = ParseQuery.getQuery("userTournament");
                userTournamentParseQuery.whereEqualTo("objectId",userTournament.getObjectId());
                userTournamentParseQuery.findInBackground(new FindCallback<UserTournament>() {
                    @Override
                    public void done(List<UserTournament> objects, ParseException e) {
                        Boolean isTeam = objects.get(0).isTeam();
                        //Set isTeam
                        if (isTeam){
                            userTournament.setIsTeam(true);
                            Log.i(TAG,"IsTeam->true");
                        }
                        else{
                            userTournament.setIsTeam(false);
                            Log.i(TAG,"IsTeam->false");
                        }
                    }
                });

                //Name of tournament and userTournament
                if (!tourName.equals(tournament.getName())){
                    userTournament.setTournName(tourName);
                    Log.i(TAG,tournament.getObjectId());
                    tournament.setTournName(tourName);
                    tournament.saveInBackground();
                }

                //Update userTournament description
                userTournament.setTournDescription(tournDescription);

                //Update Photo
                if (result!=null){
                    userTournament.setLogo(new ParseFile(result));
                }

                userTournament.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null){
                            Log.e(TAG,"error while creating",e);
                            Toast.makeText(EditTournamentActivity.this,"Couldn't update tournament", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(EditTournamentActivity.this, "Update succesful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    photoFile  = BitmapFactory.decodeStream(in);
                    in.close();
                    ivTournLogo.setImageBitmap(photoFile);

                    Uri selectedImageUri = data.getData();

                    File f=new File(this.getCacheDir(),"file name");
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Convert bitmap to byte array
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    result = f;
                }catch(Exception e) {
                    Toast.makeText(this, "Error while retrieving picture, try again", Toast.LENGTH_SHORT);
                }
            }
        }

    }
}
