package com.example.sc2infoapp;

import android.content.Intent;
import android.database.Cursor;
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

    private static int RESULT_LOAD_IMG = 1;
    private Bitmap photoFile;
    private File result;


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
        ivTournLogo = findViewById(R.id.ivTournLogo);

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

                Log.i(TAG,photoFile.toString());
                userTournament.setLogo(new ParseFile(result));

//                userTournament.setLogo(new ParseFile(photoFile));

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
