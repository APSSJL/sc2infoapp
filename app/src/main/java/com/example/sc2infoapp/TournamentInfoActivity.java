package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import adapters.TournamentMatchAdapter;
import interfaces.IFollowable;
import interfaces.IMatch;
import interfaces.IRateable;
import models.TaskRunner;
import models.Tournament;


@RequiresApi(api = Build.VERSION_CODES.O)
public class TournamentInfoActivity extends AppCompatActivity {

    public static final String TAG = "TournamentInfoActivity";
    Tournament tournament;
    ArrayList<IMatch> playoffs;
    TournamentMatchAdapter adapter;

    Button btnTornComment;
    Button btnTornEdit;
    Button btnTornFollow;
    Button btnTornRate;
    ImageView ivProfileImage;
    ImageView ivTornPicture;
    RatingBar rbTournament;
    RecyclerView rvTornMatches;
    TextView tvTornName;
    TextView tvTornRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_info);

        tournament = Parcels.unwrap(getIntent().getParcelableExtra("tournament"));

        btnTornComment = findViewById(R.id.btnTornComment);
        btnTornEdit = findViewById(R.id.btnTornEdit);
        btnTornFollow = findViewById(R.id.btnTornFollow);
        btnTornRate = findViewById(R.id.btnTornRate);
        ivProfileImage = findViewById(R.id.ivProfileSmall);
        ivTornPicture = findViewById(R.id.ivTornPicture);
        rbTournament = findViewById(R.id.rbTournament);
        rvTornMatches = findViewById(R.id.rvTornMatches);
        tvTornName = findViewById(R.id.tvTornName);
        tvTornRules = findViewById(R.id.tvTornRules);

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

        rbTournament.setRating(tournament.getRating());
        tvTornName.setText(tournament.getTitle());
        tvTornRules.setText(tournament.getContent());

        btnTornComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TournamentInfoActivity.this, TournamentCommentActivity.class);
                i.putExtra("title", tournament.getName());
                TournamentInfoActivity.this.startActivity(i);
            }
        });

        if (tournament.getUserCreated() == null || ParseUser.getCurrentUser() != tournament.getUserCreated().getOrganizer()) {
            btnTornEdit.setVisibility(View.GONE);
        } else {
            btnTornEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Todo: Edit Tournament Screen
                }
            });
        }


        btnTornFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((IFollowable) tournament).setFollow()) {
                    Log.i(TAG, "Follow successfully: " + tournament.getName());
                    Toast.makeText(TournamentInfoActivity.this, String.format("Successfully followed: " + tournament.getName()), Toast.LENGTH_SHORT);
                } else {
                    Log.i(TAG, "Already followed: " + tournament.getName());
                    Toast.makeText(TournamentInfoActivity.this, String.format("Already followed: " + tournament.getName()), Toast.LENGTH_SHORT);
                }
            }
        });

        btnTornRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TournamentInfoActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        playoffs = new ArrayList<>();
        adapter = new TournamentMatchAdapter(playoffs, this);

        rvTornMatches.setAdapter(adapter);
        rvTornMatches.setLayoutManager(new LinearLayoutManager(this));

        if(tournament.getUserCreated() == null) {
            getUpcomingMatches();
            TaskRunner taskRunner = new TaskRunner();
            taskRunner.executeAsync(new TournamentInfoActivity.matchTask(tournament.getName()), (data) -> {
                Log.i("Tournament Matches:", data.toString());
                try {
                    playoffs.addAll(TournamentMatchAdapter.fromJSONArray(data));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

        } else {
            if(tournament.getUserCreated().isTeam()) {
                playoffs.addAll(TournamentMatchAdapter.fromParsePlayerMatch(tournament.getUserCreated().getMatches()));
                adapter.notifyDataSetChanged();
            } else {
                playoffs.addAll(TournamentMatchAdapter.fromParsePlayerMatch(tournament.getUserCreated().getMatches()));
                adapter.notifyDataSetChanged();
            }
        }

    }

    public void showDialog() {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final RatingBar ratingBar = new RatingBar(this);
        ratingBar.setMax(5);

        popDialog.setTitle("Rate this match!");
        popDialog.setView(ratingBar);

        popDialog.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((IRateable)tournament).setRate((float)ratingBar.getRating());
                Toast.makeText(TournamentInfoActivity.this, "Successfully rated match!", Toast.LENGTH_SHORT);
                dialogInterface.dismiss();
            }
        });

        popDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        popDialog.create();
        popDialog.show();
    }

    private void getUpcomingMatches(){
        Log.i(TAG, "Upcoming tournament matches received");
        try {
            LiquipediaParser parser = new LiquipediaParser();
            playoffs.addAll(parser.parseUpcomingTournamentMatches(Jsoup.parse(MainActivity.client.getMatches()), tournament.getName()));
            adapter.notifyDataSetChanged();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public class matchTask implements Callable<JSONArray> {
        private final String input1;

        public matchTask(String input1) {
            this.input1 = input1;
        }

        @Override
        public JSONArray call() throws IOException, JSONException {
            // Some long running task
            String id = MainActivity.aligulacClient.getTournamentId(input1);
            Log.i(TAG, id);
            return MainActivity.aligulacClient.getTournamentResults(id);
        }
    }
}