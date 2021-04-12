package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class HomeFilterActivity extends AppCompatActivity {

    SharedPreferences.Editor edit;
    SharedPreferences pref;
    CheckBox player;
    CheckBox team;
    CheckBox post;
    CheckBox tournament;
    CheckBox match;
    TextView tvHiddenCat;
    Button btnBanCategory;
    Button btnUnbanCategory;
    Spinner spCategories;
    TextView tvBannedTags;
    EditText edRating;
    Button btnSave;
    Button btnBack;
    Button btnBanTag;
    Button btnUnbanTag;
    EditText edBan;
    EditText edUnban;
    ArrayList<String> hiddenCategories;
    ArrayList<String> bannedTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_filter);

        edRating = findViewById(R.id.edRating);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        player = findViewById(R.id.checkboxPlayer);
        team = findViewById(R.id.checkBoxTeam);
        post = findViewById(R.id.checkBoxPost);
        tournament = findViewById(R.id.checkBoxTournament);
        match = findViewById(R.id.checkBoxMatch);
        tvBannedTags = findViewById(R.id.tvBannedTags);
        btnBanCategory = findViewById(R.id.btnBanCat);
        btnUnbanCategory = findViewById(R.id.btnUnbanCat);
        spCategories = findViewById(R.id.spCategories);
        btnBanTag = findViewById(R.id.btnBan);
        btnUnbanTag = findViewById(R.id.btnUnban);
        edBan = findViewById(R.id.edBan);
        edUnban = findViewById(R.id.edUnban);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        edit = pref.edit();

        setFilteredTypes();
        setHiddenCategories();
        setBannedTags();
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeFilterActivity.this, MainActivity.class);
                startActivity(i);
            }
        });


        edRating.setText(String.valueOf(pref.getInt("rating", 0)));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                saveFilteredTypes();
                edit.putInt("rating", Integer.parseInt(edRating.getText().toString()));
                edit.putString("hidcat", String.join(",", hiddenCategories));
                edit.putString("bantag", String.join(",", bannedTags));
                edit.commit();
                Toast.makeText(HomeFilterActivity.this, "Preferences saved", Toast.LENGTH_SHORT).show();
            }
        });

        banButtonsSetup();

    }
    
    

    private void banButtonsSetup() {
        btnBanTag.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                String tag = edBan.getText().toString();
                if(!bannedTags.contains(tag))
                {
                    bannedTags.add(tag);
                    tvBannedTags.setText(String.format("Banned tags: %s", String.join(",", bannedTags)));
                }
            }
        });

        btnUnbanTag.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                String tag = edUnban.getText().toString();
                if(!bannedTags.contains(tag))
                    return;
                bannedTags.remove(tag);
                tvBannedTags.setText(String.format("Banned tags: %s", String.join(",", bannedTags)));
            }
        });

        btnBanCategory.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String category = spCategories.getSelectedItem().toString();
                if(!hiddenCategories.contains(category))
                {
                    hiddenCategories.add(category);
                    tvHiddenCat.setText(String.format("Hidden categories: %s", String.join(",", hiddenCategories)));
                }
            }
        });

        btnUnbanCategory.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                String category = spCategories.getSelectedItem().toString();
                if(!hiddenCategories.contains(category))
                    return;
                hiddenCategories.remove(category);
                tvHiddenCat.setText(String.format("Hidden categories: %s", String.join(",", hiddenCategories)));
            }
        });
    }

    protected void setHiddenCategories()
    {
        String hidcat = pref.getString("hidcat", "");
        hiddenCategories = new ArrayList<>();
        if(!hidcat.equals(""))
            Collections.addAll(hiddenCategories, hidcat.split(","));
        tvHiddenCat = findViewById(R.id.tvCat);

        tvHiddenCat.setText(String.format("Hidden categories: %s", hidcat));
    }

    protected void setBannedTags()
    {
        String bantag = pref.getString("bantag", "");
        bannedTags = new ArrayList<>();
        if(!bantag.equals(""))
            Collections.addAll(bannedTags, bantag.split(","));
        tvBannedTags.setText(String.format("Banned tags: %s", bantag));
    }

    protected void setFilteredTypes()
    {
        player.setChecked(pref.getBoolean("player", false));
        team.setChecked(pref.getBoolean("team", false));
        post.setChecked(pref.getBoolean("post", false));
        tournament.setChecked(pref.getBoolean("tournament", false));
        match.setChecked(pref.getBoolean("match", false));
    }

    protected void saveFilteredTypes()
    {


        edit.putBoolean("player", player.isChecked());
        edit.putBoolean("team", team.isChecked());
        edit.putBoolean("post", post.isChecked());
        edit.putBoolean("tournament", tournament.isChecked());
        edit.putBoolean("match", match.isChecked());
    }
}