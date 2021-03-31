package com.example.sc2infoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.parse.ParseUser.getCurrentUser;

public class UpdateProfileActivity extends AppCompatActivity {

    public static final String TAG = "UpdateProfileActivity";
    public static final int CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE = 42;

    Button btnUploadImage;
    Button btnProfileUpdate;
    Button btnProfileCancel;
    EditText etMMR;
    EditText etUserName;
    EditText etUserInfo;
    ImageView ivProfileImage;
    Spinner spRaces;

    ParseUser user;

    private String changedRace;
    private String selectedRace;
    private Bitmap photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //get current user
        user = getCurrentUser();

        //find view by id
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnProfileUpdate = findViewById(R.id.btnProfileUpdate);
        btnProfileCancel = findViewById(R.id.btnProfileCancel);
        etMMR = findViewById(R.id.etMMR);
        etUserName = findViewById(R.id.etUserName);
        etUserInfo = findViewById(R.id.etUserInfo);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        spRaces = findViewById(R.id.spRaces);

        //Retrieve previous info
        etMMR.setText(String.valueOf(user.getInt("MMR")));
        etUserName.setText(user.getUsername());
        etUserInfo.setText(user.getString("userInfo"));
        try {
            ParseFile p = (user.getParseFile("pic"));
            if (p != null) {
                Log.i(TAG, "loaded");
                Glide.with(this).load(p.getFile()).transform(new CircleCrop()).into(ivProfileImage);
            } else {
                Log.i(TAG, "null");
                Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivProfileImage);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        selectedRace = user.getString("inGameRace");

        if (selectedRace.isEmpty()) {
            spRaces.setSelection(3);
        } else {
            if (selectedRace.equals("Terran")) {spRaces.setSelection(0);}
            if (selectedRace.equals("Protoss")) {spRaces.setSelection(1);}
            if (selectedRace.equals("Zerg")) {spRaces.setSelection(2);}
            if (selectedRace.equals("Random")) {spRaces.setSelection(3);}
        }



        //set onClickListener: btnUploadImage
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Same codes that we used in Parstagram
                //Still figuring out how to attrive local file not creating new file
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE);
            }
        });

        //set onClickListener: btnProfileUpdate
        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve all information inserted
                int userMMR = Integer.parseInt(etMMR.getText().toString());
                String userName = etUserName.getText().toString();
                String userInfo = etUserInfo.getText().toString();

                //return updating if username is empty
                if (userName.isEmpty()) {
                    Toast.makeText(UpdateProfileActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //put request for changed info

                user.put("MMR", userMMR);
                user.put("username", userName);
                user.put("userInfo", userInfo);
                user.put("inGameRace", changedRace);


                //Update info
                user.saveInBackground(e -> {
                    if (e != null) {
                        Log.e(TAG, "Error while updating user", e);
                    }
                    //Exit activity if success
                    Log.i(TAG, "Updated user successfully");
                    finish();
                });
            }
        });

        //set onClickListener: btnProfileCancel
        btnProfileCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Alert Dialogue
                AlertDialog.Builder alert = new AlertDialog.Builder(UpdateProfileActivity.this);
                alert.setTitle("Alert!");
                alert.setMessage(String.format("Are you sure you want to leave this page?%nUnsaved contents will be lost!"));

                //Yes / No buttons
                alert.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        //Exit Activity
                        finish();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Return to activity
                    }
                });
                alert.show();
            }
        });

        //set setOnItemClickListener: spRaces
        spRaces.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changedRace = parent.getItemAtPosition(position).toString();
                spRaces.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE_FROM_GALLERY_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    photoFile  = BitmapFactory.decodeStream(in);
                    in.close();
                    ivProfileImage.setImageBitmap(photoFile);

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


                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("pic", new ParseFile(f));


                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i(TAG, "Image saved!");
                        }
                    });
                }catch(Exception e) {
                    Toast.makeText(this, "Error while retrieving picture, try again", Toast.LENGTH_SHORT);
                }
            }
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled choosing profile picture", Toast.LENGTH_SHORT).show();
            }
        }
    }


}