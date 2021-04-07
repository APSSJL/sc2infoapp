package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;

import interfaces.IPublished;

public class SearchActivity extends AppCompatActivity {

    ChipGroup cg;
    Chip all;
    EditText edSearch;
    CheckBox isHiring;
    ImageView ivProfilePicture;
    Button btnSearch;
    RecyclerView rvResults;
    ArrayList<IPublished> searchResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        edSearch = findViewById(R.id.edSearch);
        isHiring = findViewById(R.id.checkboxIsHiring);
        ivProfilePicture = findViewById(R.id.ivProfileImage);
        btnSearch = findViewById(R.id.btnSearch);
        rvResults = findViewById(R.id.rvResults);

        all = findViewById(R.id.chipAll);
        cg = findViewById(R.id.cgCategories);
        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    cg.clearCheck();
                }
            }
        });

        try {
            ParseFile p = (ParseUser.getCurrentUser().getParseFile("pic"));
            if(p != null)
            {
                Glide.with(this).load(p.getFile()).transform(new CircleCrop()).into(ivProfilePicture);
            }
            else {
                Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivProfilePicture);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

    }

    private static void search()
    {}

}