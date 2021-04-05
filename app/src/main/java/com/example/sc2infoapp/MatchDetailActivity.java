package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.tabs.TabLayout;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import fragments.MatchH2HFragment;
import fragments.MatchInfoFragment;
import fragments.MatchRankingFragment;

public class MatchDetailActivity extends AppCompatActivity {

    public static final String TAG = "MatchDetailActivity";

    ImageView ivProfileImage;
    TextView tvMatchName;
    TabLayout tlMatchTab;

    private MatchInfoFragment matchInfoFragment;
    private MatchRankingFragment matchRankingFragment;
    private MatchH2HFragment matchH2HFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        ivProfileImage = findViewById(R.id.ivProfileSmall);
        tvMatchName = findViewById(R.id.tvMatchName);
        tlMatchTab = findViewById(R.id.tlMatchTab);

        matchInfoFragment = new MatchInfoFragment();
        matchRankingFragment = new MatchRankingFragment();
        matchH2HFragment = new MatchH2HFragment();

        try {
            ParseFile p = (ParseUser.getCurrentUser().getParseFile("pic"));
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

        //TODO: Match info

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MatchDetailActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.flMatchFragment, matchInfoFragment).commit();
        tlMatchTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = matchInfoFragment;
                switch (tab.getPosition()) {
                    case 1:
                        selected = matchInfoFragment;
                        break;
                    case 2:
                        selected = matchRankingFragment;
                        break;
                    case 3:
                        selected = matchH2HFragment;
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.flMatchFragment, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


    }
}