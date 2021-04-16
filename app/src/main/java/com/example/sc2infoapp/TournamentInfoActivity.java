package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import adapters.TournamentMatchAdapter;
import interfaces.IFollowable;
import interfaces.IMatch;
import interfaces.IRateable;
import models.Match;
import models.TaskRunner;
import models.TeamMatch;
import models.Tournament;
import models.UserTournament;


@RequiresApi(api = Build.VERSION_CODES.O)
public class TournamentInfoActivity extends AppCompatActivity {

    public static final String TAG = "TournamentInfoActivity";
    ArrayList<IMatch> playoffs;
    Tournament tournament;
    UserTournament userTournament;
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

    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_info);

//        swipeContainer = findViewById(R.id.swipeContainer);

        btnTornComment = findViewById(R.id.btnTornComment);
        btnTornEdit = findViewById(R.id.btnTornEdit);
        btnTornFollow = findViewById(R.id.btnTornFollow);
        btnTornRate = findViewById(R.id.btnTornRate);
        ivProfileImage = findViewById(R.id.ivProfileSmall);
        ivTornPicture = findViewById(R.id.ivTornPicture);
        rbTournament = findViewById(R.id.rbTournamentRate);
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
                Glide.with(this).load(R.drawable.no_image).transform(new CircleCrop()).into(ivProfileImage);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnTornEdit.setVisibility(View.GONE);

        playoffs = new ArrayList<>();
        adapter = new TournamentMatchAdapter(playoffs, this);

        rvTornMatches.setAdapter(adapter);
        rvTornMatches.setLayoutManager(new LinearLayoutManager(this));

        if(Parcels.unwrap(getIntent().getParcelableExtra("userCreated"))) {
            userTournament = Parcels.unwrap(getIntent().getParcelableExtra("tournament"));

            ParseQuery<Tournament> query = ParseQuery.getQuery(Tournament.class);
            query.whereEqualTo("userCreated", userTournament);
            query.findInBackground(new FindCallback<Tournament>() {
                @Override
                public void done(List<Tournament> objects, ParseException e) {
                    tournament = objects.get(0);
                    Log.i(TAG, "userTournament Received: "+ objects.get(0).getName());

                    rbTournament.setRating(tournament.getRating());
                    tvTornName.setText(tournament.getTitle());
                    tvTornRules.setText(userTournament.getDescription());
                    File p = userTournament.getImage();
                    if (p != null) {
                        Glide.with(TournamentInfoActivity.this).load(userTournament.getImage()).into(ivTornPicture);
                    } else {
                        Glide.with(TournamentInfoActivity.this).load(R.drawable.no_image).into(ivTornPicture);
                    }


                    if (ParseUser.getCurrentUser().getObjectId().equals(userTournament.getOrganizer().getObjectId())) {
                        Log.i(TAG,"in");
                        btnTornEdit.setVisibility(View.VISIBLE);
                        btnTornEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Todo: Edit Tournament Screen
                                Intent i = new Intent(TournamentInfoActivity.this,EditTournamentActivity.class);
                                i.putExtra("userTournament", Parcels.wrap(userTournament));
                                i.putExtra("tournament",Parcels.wrap(tournament));
                                startActivity(i);
                            }
                        });
                    }
                }
            });

            if(userTournament.isTeam()) {
                for (String id : userTournament.getMatches()) {
                    ParseQuery<TeamMatch> q2 = ParseQuery.getQuery(TeamMatch.class);
                    q2.include("objectId");
                    q2.whereEqualTo("objectId", id);
                    q2.findInBackground(new FindCallback<TeamMatch>() {
                        @Override
                        public void done(List<TeamMatch> objects, ParseException e) {
                            Log.i(TAG, objects.get(0).getOpponent());
                            playoffs.addAll(objects);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            } else {
                for (String id : userTournament.getMatches()) {
                    ParseQuery<Match> q2 = ParseQuery.getQuery(Match.class);
                    q2.include("objectId");
                    q2.whereEqualTo("objectId", id);
                    q2.findInBackground(new FindCallback<Match>() {
                        @Override
                        public void done(List<Match> objects, ParseException e) {
                            Log.i(TAG, objects.get(0).getOpponent());
                            playoffs.addAll(objects);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

        } else {
            getExternalTournament();
        }


        btnTornComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TournamentInfoActivity.this, TournamentCommentActivity.class);
                i.putExtra("title", tournament.getName());
                TournamentInfoActivity.this.startActivity(i);
            }
        });

        btnTornFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((IFollowable) tournament).setFollow()) {
                    Log.i(TAG, "Follow successfully: " + tournament.getName());
                    Toast.makeText(TournamentInfoActivity.this, String.format("Successfully followed: " + tournament.getName()), Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Already followed: " + tournament.getName());
                    Toast.makeText(TournamentInfoActivity.this, String.format("Already followed: " + tournament.getName()), Toast.LENGTH_SHORT).show();
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

    }

    public void showDialog() {
        AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        RatingBar ratingBar = new RatingBar(this);
        RelativeLayout relativeLayout = new RelativeLayout(TournamentInfoActivity.this);

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5f);
        ratingBar.setMax(5);

        RelativeLayout.LayoutParams ratingBarParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ratingBarParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ratingBar.setLayoutParams(ratingBarParam);
        relativeLayout.addView(ratingBar);

        popDialog.setTitle("Rate this tournament!");
        popDialog.setView(relativeLayout);

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


    public void getExternalTournament(){
        String title = getIntent().getStringExtra("tournament");

        TaskRunner taskRunner = new TaskRunner();
        LiquipediaParser parser = new LiquipediaParser();

        taskRunner.executeAsync(new getMatches(title), (data) -> {

            tournament = new Tournament();
            tournament.setTitle(title);
            tvTornRules.setText(String.format(parser.getTournamentRules(data).replaceAll("abbr\\\\/", "").replaceAll("\\\\n ", "%n").replaceAll("\\\\n", "%n")));

            playoffs.addAll(parser.getTournamentMatches(data));
            ArrayList<IMatch> toRemove = new ArrayList<>();
            for (IMatch iMatch : playoffs){
                try {
                    String test = iMatch.getOpponent().split(" vs ")[1];
                } catch(ArrayIndexOutOfBoundsException e) {
                    toRemove.add(iMatch);
                }
            }
            playoffs.removeAll(toRemove);
            adapter.notifyDataSetChanged();

            rbTournament.setRating(tournament.getRating());
            tvTornName.setText(tournament.getTitle());

            ParseQuery<Tournament> query = ParseQuery.getQuery(Tournament.class);
            query.whereEqualTo("name", title);
            query.findInBackground(new FindCallback<Tournament>() {
                @Override
                public void done(List<Tournament> objects, ParseException e) {
                    if(objects.isEmpty()) {
                        Log.i(TAG, "New tournament");
                        tournament.saveInBackground();
                    } else {
                        Log.i(TAG, "Tournament already exist");
                    }
                }
            });
        });

        taskRunner.executeAsync(new getImage(title), (data) -> {
            try {
                Thread.sleep(2000);
                String photoLink = parser.getPhotoLink(data);
                Glide.with(TournamentInfoActivity.this).load(photoLink).into(ivTornPicture);
            } catch (InterruptedException | JSONException e) {
                Glide.with(TournamentInfoActivity.this).load(R.drawable.no_image).into(ivTornPicture);
                e.printStackTrace();
            }
        });

    }

    class getMatches implements Callable<String> {
        private final String input;

        public getMatches(String input) {
            this.input = input;
        }

        @Override
        public String call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.client.getUnparsed(input);
        }
    }

    class getImage implements Callable<JSONObject> {
        private final String input;

        public getImage(String input) {
            this.input = input;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.client.getFullPage(input);
        }
    }

}