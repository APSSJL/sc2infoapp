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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
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
import java.util.List;

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

    UserTournament userTournament;
    Tournament tournament;

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

        user = ParseUser.getCurrentUser();

        btnPostTournLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE);
            }
        });

        btnSaveTourn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tourName = etTournName.getText().toString();
                String tournDescription = etTournDescription.getText().toString();
                Boolean isTeam = cbIsTeam.isChecked();

                //Name cannot be empty
                if (tourName.isEmpty()){
                    Toast.makeText(EditTournamentActivity.this,"Tournament Name cannot be empty!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //Set isTeam
                if (isTeam){
                    userTournament.setIsTeam(true);
                    Log.i(TAG,"IsTeam->true");
                }
                else{
                    userTournament.setIsTeam(false);
                    Log.i(TAG,"IsTeam->false");
                }

                //Name of tournament and userTournament
                if (!tourName.equals(tournament.getName())){
                    userTournament.setTournName(tourName);
                    Log.i(TAG,tournament.getObjectId());
                    tournament.setTournName(tourName);
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
