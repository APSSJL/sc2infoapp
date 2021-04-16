package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import fragments.MatchH2HFragment;
import fragments.MatchInfoFragment;
import fragments.MatchRankingFragment;
import interfaces.IFollowable;
import interfaces.IMatch;
import models.Player;
import models.TaskRunner;
import models.Team;

public class MatchDetailActivity extends AppCompatActivity {

    public static final String TAG = "MatchDetailActivity";
    public static final String IMAGE_BASE_URL = "https://liquipedia.net";

    Button btnFollow;
    ImageView ivProfileImage;
    ImageView ivOpponentLeft;
    ImageView ivOpponentRight;
    TextView tvOpponentLeft;
    TextView tvOpponentRight;
    TabLayout tlMatchTab;
    List<String> country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        btnFollow = findViewById(R.id.btnMatchFollow);
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
                Glide.with(this).load(R.drawable.no_image).transform(new CircleCrop()).into(ivProfileImage);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        IMatch match = Parcels.unwrap(getIntent().getParcelableExtra("match"));

        String opponentLeft = match.getOpponent().split(" vs ")[0];
        String opponentRight = match.getOpponent().split(" vs ")[1];
        country = new ArrayList<>();

        tvOpponentLeft.setText(opponentLeft);
        tvOpponentRight.setText(opponentRight);

        switch (match.getMatchType()) {
            case IMatch.EXTERNAL:
                try {
                    getExternalTeam(opponentLeft, ivOpponentLeft);
                    getExternalTeam(opponentRight, ivOpponentRight);
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

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((IFollowable) match).setFollow()) {
                    Log.i(TAG, "Follow successfully: " + match.getOpponent());
                    Toast.makeText(MatchDetailActivity.this, "Successfully followed: " + match.getOpponent(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Already followed: " + match.getOpponent());
                    Toast.makeText(MatchDetailActivity.this, "Already followed: " + match.getOpponent(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        MatchInfoFragment matchInfoFragment = new MatchInfoFragment(match, opponentLeft, opponentRight);
        MatchRankingFragment matchRankingFragment = new MatchRankingFragment(match, opponentLeft, opponentRight);
        MatchH2HFragment matchH2HFragment = new MatchH2HFragment(match, opponentLeft, opponentRight);
        getSupportFragmentManager().beginTransaction().replace(R.id.flMatchFragment, matchInfoFragment).commit();
        tlMatchTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected;
                switch (tab.getPosition()) {
                    case 0:
                        selected = matchInfoFragment;
                        break;
                    case 1:
                            selected = matchRankingFragment;
                        break;
                    case 2:
                        selected = matchH2HFragment;
                        break;
                    default:
                        selected = matchInfoFragment;
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

    public void getExternalTeam(String opponent, ImageView target) throws ExecutionException, InterruptedException {
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new ImageTask(opponent), (data) ->
        {
            try {
                Thread.sleep(3000);
                Document doc = Jsoup.parse(data.getString("text"));
                LiquipediaParser parser = new LiquipediaParser();
                String link = parser.getPhotoLink(data);
                country.add(parser.getCountry(doc));
                Glide.with(MatchDetailActivity.this).load(link).transform(new CircleCrop()).into(target);
                Log.i(TAG, link);
            } catch (JSONException | InterruptedException e) {
                Glide.with(MatchDetailActivity.this).load(R.drawable.no_image).transform(new CircleCrop()).into(target);
                e.printStackTrace();
            }

        });
    }

    class ImageTask implements Callable<JSONObject> {
        private final String input;

        public ImageTask(String input) {
            this.input = input;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            return (MainActivity.client.getFullPage(input));
        }
    }

    public void getParsePlayer(String left, String right) {
        ParseQuery<Player> query1 = ParseQuery.getQuery(Player.class);
        ParseQuery<Player> query2 = ParseQuery.getQuery(Player.class);
        query1.whereEqualTo("name", left);
        query2.whereEqualTo("name", right);

        try {
            List<Player> x = query1.find();
            List<Player> y = query2.find();
            Player p1 = query1.find().get(0);
            Player p2 = query2.find().get(0);

            try {
                ParseFile file = (p1.getParseFile("picture"));
                if (p1 != null && file != null) {
                    Log.i(TAG, "loaded");
                    file.getFile();
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentLeft);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.no_image).transform(new CircleCrop()).into(ivOpponentLeft);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                ParseFile file = (p2.getParseFile("picture"));
                if (p2 != null && file != null) {
                    Log.i(TAG, "loaded");
                    file.getFile();
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentLeft);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.no_image).transform(new CircleCrop()).into(ivOpponentLeft);
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
        query1.whereEqualTo("teamName", left);
        query2.whereEqualTo("teamName", right);

        try {
            Team t1 = query1.find().get(0);
            Team t2 = query2.find().get(0);

            try {
                ParseFile file = (t1.getParseFile("picture"));

                if (t1 != null && file != null) {
                    file.getFile();
                    Log.i(TAG, "loaded");
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentLeft);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.no_image).transform(new CircleCrop()).into(ivOpponentLeft);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                ParseFile file = (t2.getParseFile("picture"));

                if (t2 != null  && file != null) {
                    file.getFile();
                    Log.i(TAG, "loaded");
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivOpponentRight);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.no_image).transform(new CircleCrop()).into(ivOpponentRight);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}