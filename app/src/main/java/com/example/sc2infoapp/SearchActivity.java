package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import adapters.UserFeedAdapter;
import interfaces.IPublished;
import models.Match;
import models.Player;
import models.Post;
import models.TaskRunner;
import models.Team;
import models.TeamMatch;
import models.Tournament;
import models.UserInfo;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SEARCH ACTIVITY";
    ChipGroup cg;
    Chip all;
    Chip chipTags;
    EditText edSearch;
    CheckBox isHiring;
    ImageView ivProfilePicture;
    Button btnSearch;
    RecyclerView rvResults;
    ArrayList<IPublished> searchResults;
    UserFeedAdapter adapter;
    Spinner spCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        edSearch = findViewById(R.id.edSearch);
        isHiring = findViewById(R.id.checkboxIsHiring);
        ivProfilePicture = findViewById(R.id.ivProfileImage);
        btnSearch = findViewById(R.id.btnSearch);
        rvResults = findViewById(R.id.rvResults);
        spCategories = findViewById(R.id.spCategories);


        if(getIntent().getBooleanExtra("teamSearch", false))
        {
            isHiring.setChecked(true);
            Chip team = findViewById(R.id.chipTeams);
            team.setChecked(true);
            SearchTeams();
        }

        searchResults = new ArrayList<>();
        adapter = new UserFeedAdapter(this, searchResults);

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        all = findViewById(R.id.chipAll);
        cg = findViewById(R.id.cgCategories);
        all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cg.clearCheck();
                }
            }
        });


        try {
            ParseFile p = (ParseUser.getCurrentUser().getParseFile("pic"));
            if (p != null) {
                Glide.with(this).load(p.getFile()).transform(new CircleCrop()).into(ivProfilePicture);
            } else {
                Glide.with(this).load(R.drawable.ic_launcher_background).transform(new CircleCrop()).into(ivProfilePicture);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                searchResults.clear();
                adapter.notifyDataSetChanged();
                List<Integer> x = cg.getCheckedChipIds();
                if(x.contains(R.id.chipTags))
                    SearchPosts(true);
                if(x.size() == 0 || x.contains(R.id.chipPlayers))
                    SearchPlayers();
                if(x.size() == 0 || x.contains(R.id.chipMatches))
                    SearchMatches();
                if(x.size() == 0 || x.contains(R.id.chipTeams))
                    SearchTeams();
                if(x.size() == 0 || x.contains(R.id.chipUsers))
                    SearchUsers();
                if(x.size() == 0 || x.contains(R.id.chipPosts))
                    SearchPosts(false);
                if(x.size() == 0 || x.contains(R.id.chipTournaments))
                    SearchTournaments();

            }
        });

    }

    public void SearchUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(5);
        query.whereContains("username", edSearch.getText().toString());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                for (ParseUser user : objects) {
                    searchResults.add(new UserInfo(user));

                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void SearchMatches()
    {
        ParseQuery<Match> queryPlayer = ParseQuery.getQuery(Match.class);
        queryPlayer.setLimit(5);
        queryPlayer.whereContains("details", edSearch.getText().toString());
        queryPlayer.addDescendingOrder("createdAt");
        queryPlayer.findInBackground(new FindCallback<Match>() {
            @Override
            public void done(List<Match> objects, ParseException e) {
                searchResults.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });

        ParseQuery<TeamMatch> queryTeam = ParseQuery.getQuery(TeamMatch.class);
        queryTeam.setLimit(5);
        queryTeam.whereContains("details", edSearch.getText().toString());
        queryTeam.addDescendingOrder("createdAt");
        queryTeam.findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                searchResults.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void SearchTeams() {
        ParseQuery<Team> query = ParseQuery.getQuery(Team.class);
        query.setLimit(5);
        if(isHiring.isChecked())
            query.whereEqualTo("isHiring", true);
        query.whereContains("teamName", edSearch.getText().toString());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Team>() {
            @Override
            public void done(List<Team> objects, ParseException e) {
                searchResults.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
        if(edSearch.getText().length() > 0) {
            TaskRunner taskRunner = new TaskRunner();

            taskRunner.executeAsync(new ExistanceTask(edSearch.getText().toString().replace(" ", "_")), (data) ->
            {
                Team t = new Team();
                t.setName(edSearch.getText().toString());
                searchResults.add(t);

            });
        }
    }

    class ExistanceTask implements Callable<Boolean> {
        private final String input;

        public ExistanceTask(String input) {
            this.input = input;
        }

        @Override
        public Boolean call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.client.checkIfPageExists(input);
        }
    }

    public void SearchTournaments() {
        ParseQuery<Tournament> query = ParseQuery.getQuery(Tournament.class);
        query.setLimit(5);
        query.include("userCreated");
        query.include("userCreated.organizer");
        query.whereContains("name", edSearch.getText().toString());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Tournament>() {
            @Override
            public void done(List<Tournament> objects, ParseException e) {
                searchResults.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void SearchPlayers() {
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
        query.setLimit(5);
        query.whereContains("name", edSearch.getText().toString());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Player>() {
            @Override
            public void done(List<Player> objects, ParseException e) {
                searchResults.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void SearchPosts(boolean tags) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.setLimit(5);
        if(!spCategories.getSelectedItem().toString().equals("All"))
        {
            query.whereEqualTo(Post.KEY_CATEGORY, spCategories.getSelectedItem().toString());
        }
        if(!tags)
        {
            query.whereContains(Post.KEY_TITLE, edSearch.getText().toString());
        }
        else
        {
            query.whereContains(Post.KEY_TAGS, edSearch.getText().toString());
        }
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                searchResults.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }


}