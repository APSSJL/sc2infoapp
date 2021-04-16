package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adapters.UserFeedAdapter;
import fragments.UserProfileFragment;
import interfaces.IPublished;
import models.Team;
import models.UserInfo;

import static com.parse.ParseUser.getCurrentUser;

public class ViewProfileActivity extends AppCompatActivity {

    private static final String TAG = "User profile";
    TextView tvName;
    TextView tvRace;
    ImageView ivPicture;
    TextView tvMmr;
    TextView tvBio;
    RecyclerView rvItems;
    TextView tvTeam;
    Button btnFollow;

    ParseUser user;
    UserFeedAdapter adapter;
    List<IPublished> published;
    Team team;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        published = new ArrayList<>();
        tvName = findViewById(R.id.tvName);
        tvRace = findViewById(R.id.tvRace);
        ivPicture = findViewById(R.id.ivPicture);
        tvMmr = findViewById(R.id.tvMmr);
        tvBio = findViewById(R.id.tvBio);
        rvItems = findViewById(R.id.rvItems);
        tvTeam = findViewById(R.id.tvTeam);
        btnFollow = findViewById(R.id.btnFollow);
        adapter = new UserFeedAdapter(this, published);

        user = getIntent().getParcelableExtra("user");


        try {
            user.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        UserInfo info = (UserInfo) user.get("Additional");
        if(info != null)
        {
            try {
                info.fetch();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            team = (Team) info.get("team");
        }

        tvName.setText(user.getUsername());
        tvRace.setText(user.getString("inGameRace"));
        tvMmr.setText(String.valueOf(user.getInt("MMR")));
        tvBio.setText(user.getString("userInfo"));


        try {
            ParseFile p = (user.getParseFile("pic"));
            if (p != null) {
                Log.i(TAG, "loaded");
                Glide.with(this).load(p.getFile()).transform(new CircleCrop()).into(ivPicture);
            } else {
                Log.i(TAG, "null");
                Glide.with(this).load(R.drawable.no_image).transform(new CircleCrop()).into(ivPicture);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvItems.setLayoutManager(manager);
        rvItems.setAdapter(adapter);

        if(team != null)
        {
            try {
                team.fetchIfNeeded();
                tvTeam.setText(team.getTeamName());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        else
        {
            tvTeam.setText("No team");
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object a = getCurrentUser().get("follows");
                ParseUser cur = ParseUser.getCurrentUser();
                if(a != null && ((ArrayList)a).contains("User:"+cur.getObjectId()))
                    return;;

                cur.add("follows", "User:"+user.getObjectId());
                cur.saveInBackground();
            }
        });

        UserProfileFragment.populateUserFeed(user, published, adapter);
    }
}