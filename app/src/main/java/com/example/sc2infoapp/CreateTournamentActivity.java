package com.example.sc2infoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import models.Team;
import models.Tournament;
import models.UserTournament;

import static com.example.sc2infoapp.UpdateProfileActivity.CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE;

public class CreateTournamentActivity extends AppCompatActivity {
    public static final String TAG = "CREATE_TOURN_ACTIVITY";

    EditText etTournName;
    EditText etTournDescription;
    Button btnPostTournLogo;
    Button btnSaveTourn;
    CheckBox cbIsTeam;
    ImageView ivTournLogo;

    //Match stuff
    EditText etObj1;
    EditText etObj2;
    EditText etMatchDescription;
    Button btnCreateMatch;

    private String Object1;
    private String Object2;

    private List<Team> teams;

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
        btnSaveTourn = findViewById(R.id.btnSaveTourn);
        etTournName = findViewById(R.id.etTournName);
        etTournDescription = findViewById(R.id.etTournDescription);
        cbIsTeam = findViewById(R.id.cbIsTeam);
        ivTournLogo = findViewById(R.id.ivTournLogo);
        etObj1 = findViewById(R.id.spObj1);
        etObj2 = findViewById(R.id.spObj2);
        etMatchDescription = findViewById(R.id.etMatchDescription);
        btnCreateMatch = findViewById(R.id.btnCreateMatch);

        etObj1.setVisibility(View.GONE);
        etObj2.setVisibility(View.GONE);
        etMatchDescription.setVisibility(View.GONE);
        btnCreateMatch.setVisibility(View.GONE);

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
        btnSaveTourn.setOnClickListener(new View.OnClickListener() {
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

                if (result!=null){
                    userTournament.setLogo(new ParseFile(result));
                }


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

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            String selectedItem = parent.getItemAtPosition(pos).toString();

            //check which spinner triggered the listener
            switch (parent.getId()) {
                //Player1 spinner
                case R.id.spObj1:
                    //make sure the country was already selected during the onCreate
                    if(Object1 != null){
                        Toast.makeText(parent.getContext(), "Player 1 you selected is " + selectedItem,
                                Toast.LENGTH_LONG).show();
                    }
                    Object1 = selectedItem;
                    break;
                //Player2 spinner
                case R.id.spObj2:
                    //make sure the animal was already selected during the onCreate
                    if(Object2 != null){
                        Toast.makeText(parent.getContext(), "Player 2 selected is " + selectedItem,
                                Toast.LENGTH_LONG).show();
                    }
                    Object2 = selectedItem;
                    break;
            }


        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

}
