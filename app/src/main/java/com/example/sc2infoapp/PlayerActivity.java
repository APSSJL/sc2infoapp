package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import adapters.MatchesAdapter;
import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.Player;
import models.TaskRunner;

public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PLAYER ACTIVITY";
    TextView tvName;
    TextView tvRace;
    TextView tvRating;
    ImageView ivPicture;
    TextView tvBio;
    RecyclerView rvMatches;
    RatingBar ratingBar;
    Button btnFollow;
    Button btnComment;
    MatchesAdapter adapter;
    ArrayList<IMatch> opponents;

    String playerName;

    AligulacClient aClient = new AligulacClient();
    LiquipediaParser parser = new LiquipediaParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        tvName = findViewById(R.id.tvPlayerName);
        tvRace = findViewById(R.id.tvPlayerRace);
        tvRating = findViewById(R.id.tvPlayerRating);
        ivPicture = findViewById(R.id.ivPlayerPhoto);
        tvBio = findViewById(R.id.tvPlayerBio);
        rvMatches = findViewById(R.id.rvMatches);
        ratingBar = findViewById(R.id.ratingBar);
        btnFollow = findViewById(R.id.btnFollow);
        btnComment = findViewById(R.id.btnComment);

        opponents = new ArrayList<>();

        adapter = new MatchesAdapter(opponents, this);

        rvMatches.setAdapter(adapter);
        rvMatches.setLayoutManager(new LinearLayoutManager(this));

        playerName = getIntent().getStringExtra("playerName");

        getLiquipediaData();
        getParseData();
        getAligulacData();



        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : navigate to comment section
            }
        });



    }

    public void getAligulacData()
    {
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new AligulacTask(playerName), (data) ->
        {
            JSONObject cur = null;
            try {
                cur = data.getJSONObject("current_rating");
                double vp = cur.getDouble("tot_vp");
                double vt = cur.getDouble("tot_vt");
                double vz = cur.getDouble("tot_vz");
                tvRating.setText(String.format("vp: %f, vt: %f, vz: %f", vp, vt, vz));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }

    public void getLiquipediaData()
    {
        TaskRunner taskRunner = new TaskRunner();
        LiquipediaParser parser = new LiquipediaParser();
        taskRunner.executeAsync(new LongRunningTask(playerName), (data) -> {
            try {
                tvRace.setText(parser.getRace(data));

                Document doc = Jsoup.parse(data.getString("text"));
                tvBio.setText(parser.getBio(doc));
                tvName.setText(String.format("%s: %s", playerName, parser.getName(doc)));
                //parser.getPhotoLink(data, playerName);]
                opponents.addAll(parser.getRecentMatches(doc, playerName));
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void getParseData()
    {
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
        query.whereEqualTo("name", playerName);

        try {
            List<Player> players = query.find();

            if(players.size() == 0)
                return;
            Player p = players.get(0);
            
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();
                    user.add("follows", p.getObjectId());
                }
            });

            try {
                ParseFile file = (p.getParseFile("picture"));
                if (p != null && file != null) {
                    Log.i(TAG, "loaded");
                    Glide.with(this).load(file.getFile()).transform(new CircleCrop()).into(ivPicture);
                } else {
                    Log.i(TAG, "null");
                    Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivPicture);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            getMatches(p);
            ratingBar.setRating((int)(p.getRating()));
            if(p.getRated())
                ratingBar.setIsIndicator(true);
            else
            {
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                        p.setRating((int)rating);
                        p.saveInBackground();
                    }
                });
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void getMatches(Player p)
    {
        ParseQuery<ParseUser> q1 = ParseUser.getQuery();

        q1.include("player");
        q1.whereEqualTo("player",p);
        try {
            List<ParseUser> users = q1.find();
            if(users.size() == 0)
                return;
            ParseUser x = users.get(0);
            ParseQuery<Match> q2 = ParseQuery.getQuery(Match.class);
            q2.include("Player2");
            q2.include("Player1");
            q2.whereEqualTo("Player2", x);
            opponents.addAll(q2.find());

            ParseQuery<Match> q3 = ParseQuery.getQuery(Match.class);
            q3.include("Player2");
            q3.include("Player1");
            q3.whereEqualTo("Player1", x);
            opponents.addAll(q3.find());
            Log.i("a","b");
            adapter.notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    class LongRunningTask implements Callable<JSONObject> {
        private final String input;

        public LongRunningTask(String input) {
            this.input = input;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.client.getFullPage(input);
        }
    }

    class AligulacTask implements Callable<JSONObject> {
        private final String input;

        public AligulacTask(String input) {
            this.input = input;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.aligulacClient.getPlayer(input);
        }
    }
}