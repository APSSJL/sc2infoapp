package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

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
    ArrayList<Pair<String,String>> opponents;

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
                //parser.getPhotoLink(data, playerName);

                opponents.addAll(parser.getRecentMatches(doc));
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
            Player p = query.find().get(0);

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();
                    user.add("follows", p.getObjectId());
                }
            });

            ratingBar.setRating((int)(p.getRating()));
            if(p.getBoolean("rated"))
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