package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fragments.MatchH2HFragment;
import fragments.MatchInfoFragment;
import fragments.MatchRankingFragment;
import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.Player;
import models.Team;
import models.TeamMatch;

public class MatchDetailActivity extends AppCompatActivity {

    public static final String TAG = "MatchDetailActivity";
    public static final String IMAGE_BASE_URL = "https://liquipedia.net";

    ImageView ivProfileImage;
    ImageView ivOpponentLeft;
    ImageView ivOpponentRight;
    TextView tvOpponentLeft;
    TextView tvOpponentRight;
    TabLayout tlMatchTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        ivProfileImage = findViewById(R.id.ivProfileSmall);
        ivOpponentLeft = findViewById(R.id.ivOpponentLeft);
        ivOpponentRight = findViewById(R.id.ivOpponentRight);
        tvOpponentLeft = findViewById(R.id.tvOpponentLeft);
        tvOpponentRight = findViewById(R.id.tvOpponentRight);
        tlMatchTab = findViewById(R.id.tlMatchTab);

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

        IMatch match = Parcels.unwrap(getIntent().getParcelableExtra("match"));
        //TODO: Match should be passed as an extra from previous screen

        String opponentLeft = match.getOpponent().split("vs")[0];
        String opponentRight = match.getOpponent().split("vs")[1];
        tvOpponentLeft.setText(opponentLeft);
        tvOpponentRight.setText(opponentRight);

        switch (match.getMatchType()) {
            case IMatch.EXTERNAL:
                try {
                    Glide.with(MatchDetailActivity.this).load(getExternalTeam(opponentLeft)).transform(new CircleCrop()).into(ivOpponentLeft);
                    Glide.with(MatchDetailActivity.this).load(getExternalTeam(opponentRight)).transform(new CircleCrop()).into(ivOpponentRight);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case IMatch.INTERNAL:
                getParsePlayer(opponentLeft, opponentRight);
                break;
            case IMatch.TEAM:
                getParseTeam(opponentLeft, opponentRight);
                break;
        }

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MatchDetailActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.flMatchFragment, new MatchInfoFragment(match)).commit();

        tlMatchTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected;
                switch (tab.getPosition()) {
                    case 0:
                        selected = new MatchInfoFragment(match);
                        break;
                    case 1:
                        selected = new MatchRankingFragment(match);
                        break;
                    case 2:
                        selected = new MatchH2HFragment();
                        break;
                    default:
                        selected = new MatchInfoFragment(match);
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

    public String getExternalTeam(String opponent) throws ExecutionException, InterruptedException {
        LiquipediaClient client = new LiquipediaClient();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {

                String imagePath = null;

                Log.i(TAG, "new thread entered");
                try {
                    imagePath = client.getPageByName(opponent);

                    imagePath = imagePath.substring(imagePath.indexOf("/commons/images/thumb/"));
                    imagePath = imagePath.substring(0, imagePath.indexOf("/600px-"));
                    imagePath = imagePath.replace("thumb/", "");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return imagePath;
            }
        };

        Future<String> future = executor.submit(callable);
        return String.format(IMAGE_BASE_URL + future.get());
    }

    public void getParsePlayer(String left, String right) {
        ParseQuery<Player> query1 = ParseQuery.getQuery(Player.class);
        ParseQuery<Player> query2 = ParseQuery.getQuery(Player.class);
        query1.whereEqualTo("name", left);
        query2.whereEqualTo("name", right);

        try {
            Player p1 = query1.find().get(0);
            Player p2 = query2.find().get(0);

            try {
                ParseFile file = (p1.getParseFile("picture"));
                if (p1 != null) {
                    Log.i(TAG, "loaded");
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentLeft);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivOpponentLeft);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                ParseFile file = (p2.getParseFile("picture"));
                if (p2 != null) {
                    Log.i(TAG, "loaded");
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentLeft);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivOpponentLeft);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void getParseTeam(String left, String right) {
        ParseQuery<Team> query1 = ParseQuery.getQuery(Team.class);
        ParseQuery<Team> query2 = ParseQuery.getQuery(Team.class);
        query1.whereEqualTo("name", left);
        query2.whereEqualTo("name", right);

        try {
            Team t1 = query1.find().get(0);
            Team t2 = query2.find().get(0);

            try {
                ParseFile file = (t1.getParseFile("picture"));
                if (t1 != null) {
                    Log.i(TAG, "loaded");
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentLeft);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivOpponentLeft);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                ParseFile file = (t2.getParseFile("picture"));
                if (t2 != null) {
                    Log.i(TAG, "loaded");
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentRight);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivOpponentRight);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}