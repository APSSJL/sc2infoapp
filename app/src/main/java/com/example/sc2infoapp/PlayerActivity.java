package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Text;

import java.io.IOException;
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
        tvBio = findViewById(R.id.tvBio);
        rvMatches = findViewById(R.id.rvMatches);
        ratingBar = findViewById(R.id.ratingBar);
        btnFollow = findViewById(R.id.btnFollow);
        btnComment = findViewById(R.id.btnComment);

        playerName = getIntent().getStringExtra("playerName");



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
                //parser.getRecentMatches(doc);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
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
}