package com.example.sc2infoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import static com.parse.ParseUser.getCurrentUser;

public class PostComposeActivity extends AppCompatActivity {

    public static final String TAG = "PostComposeActivity";

    Button btnPostSubmit;
    Button btnPostCancel;
    EditText etTitle;
    EditText etContent;
    EditText etTags;
    Spinner spCategories;

    ParseUser user;

    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_compose);

        //get current user
        user = getCurrentUser();

        //find view by id
        btnPostSubmit = findViewById(R.id.btnPostSubmit);
        btnPostCancel = findViewById(R.id.btnPostCancel);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etTags = findViewById(R.id.etTags);
        spCategories = findViewById(R.id.spCategories);
        spCategories.setSelection(0);

        //set onClickListener:
        btnPostSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
                String tags = etTags.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(PostComposeActivity.this, "Title cannot be empty!", Toast.LENGTH_SHORT);
                }
                if (content.isEmpty()) {
                    Toast.makeText(PostComposeActivity.this, "Content cannot be empty!", Toast.LENGTH_SHORT);
                }

                //Create new post, use set methods to save
                Post post = new Post();
                post.setAuthor(user);
                post.setTitle(title);
                post.setContent(content);
                post.setCategory(spCategories.getSelectedItem().toString());
                post.setTags(tags);

                //saveInBackground
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while posting", e);
                            Toast.makeText(PostComposeActivity.this, "Error while posting", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "Posted successfully");
                        finish();
                    }
                });

            }
        });

        //set onClickListener:btnPostCancel
        btnPostCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Alert Dialogue
                AlertDialog.Builder alert = new AlertDialog.Builder(PostComposeActivity.this);
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

        //set setOnItemClickListener: spCategories

    }
}