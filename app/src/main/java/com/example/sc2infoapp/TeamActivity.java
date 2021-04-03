package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import adapters.ExternalTeamAdapter;
import adapters.TeamMatchAdapter;
import adapters.TeamPlayerAdapter;
import interfaces.IMatch;
import models.ExternalMatch;
import models.Match;
import models.Player;
import models.TaskRunner;
import models.Team;
import models.TeamMatch;

public class TeamActivity extends AppCompatActivity {

    private static final String TAG = "TEAM ACTIVITY";
    TextView tvTeamName;
    TextView tvTeamInfo;
    TextView tvHiring;
    Button btnJoin;
    Button btnManage;
    RecyclerView rvRoster;
    String teamName;
    RatingBar rb;
    RecyclerView rvMatches;
    ArrayList<IMatch> matches;
    TeamMatchAdapter matchAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        tvTeamName = findViewById(R.id.tvTeamName);
        tvTeamInfo = findViewById(R.id.tvTeamInfo);
        btnJoin = findViewById(R.id.btnAskJoin);
        tvHiring = findViewById(R.id.tvHiring);
        btnManage = findViewById(R.id.btnManage);
        rvRoster = findViewById(R.id.rvRoster);
        rb = findViewById(R.id.ratingBar);
        rvMatches = findViewById(R.id.rvTeamMatches);



        matches = new ArrayList<>();;
        matchAdapter = new TeamMatchAdapter(this, matches);

        rvMatches.setLayoutManager(new LinearLayoutManager(this));
        rvMatches.setAdapter(matchAdapter);

        teamName = getIntent().getStringExtra("teamName");
        Team t = tryGetTeam(teamName);

        if(t != null)
        {
            getMatches(t);
            rb.setRating((int)(t.getRating()));
            if(t.getRated())
                rb.setIsIndicator(true);
            else
            {
                rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                        t.setRating((int)rating);
                        t.saveInBackground();
                    }
                });
            }
        }
        else
        {
            rb.setVisibility(View.GONE);
        }

        getLiquipediaMatches();

        if(t != null && t.getOwner() != null)
        {
            setupInternalTeam(t);
        }
        else
        {
            getInfoFromApi();
        }

    }

    private void getLiquipediaMatches()
    {
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new MatchesTask(teamName), (data) ->
        {
            JSONObject cur = null;
            try {
                LiquipediaParser parser = new LiquipediaParser();
                ArrayList<Pair<String, String>> matchList = parser.getRecentMatches(Jsoup.parse(data.getString("text")));
                for(Pair<String,String> m : matchList )
                {
                    matches.add(new ExternalMatch(m.first, m.second));
                }
                matchAdapter.notifyDataSetChanged();
                Log.i(TAG, "parsed");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        });
    }

    private void getInfoFromApi() {
        btnJoin.setVisibility(View.GONE);
        btnManage.setVisibility(View.GONE);
        tvHiring.setVisibility(View.GONE);
        tvTeamName.setText(teamName);
        ArrayList<Pair<String, String>> roster = new ArrayList<>();
        ExternalTeamAdapter adapter = new ExternalTeamAdapter(roster, this);
        TaskRunner taskRunner = new TaskRunner();

        taskRunner.executeAsync(new LongRunningTask(teamName), (data) -> {
            // MyActivity activity = activityReference.get();
            // activity.progressBar.setVisibility(View.GONE);
            // populateData(activity, data) ;
            Log.i("main", "ies alive");
            Document doc = Jsoup.parse(data);
            Elements x = doc.select("p,ul");
            StringBuilder sb = new StringBuilder();
            int size = x.size();
            getRoster(doc, roster);
            for (int i = 2; i < size - 1; i++)
            {
                String v = x.get(i).text();
                if(v.startsWith("Best Yearly") || v.startsWith("1 History"))
                    break;
                sb.append(v);
                Log.i(TAG, String.valueOf(x.get(i).text()));
            }
            tvTeamInfo.setText(sb.toString());
            adapter.notifyDataSetChanged();
            Log.i("TEAM ACTIVITY", sb.toString());

        });
        rvRoster.setLayoutManager(new LinearLayoutManager(this));
        rvRoster.setAdapter(adapter);
    }

    public void getRoster(Document doc, ArrayList<Pair<String, String>> resList)
    {
        Elements t = doc.select("table").select("tbody");
        Element res = null;
        for(Element e : t)
        {
            Element y = e.selectFirst("tr").selectFirst("th");
            if(y != null && y.text().equals("Active Squad"))
            {
                res = e;
                break;
            }
        }
        Elements x = res.select("tr");
        int size = x.size();
        for(int i = 2; i < size; i++)
        {

            Elements rows = x.get(i).select("td");
            String name = (rows.get(2).text());
            String race = (rows.get(1).select("a").attr("title"));
            resList.add(new Pair<>(name, race));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupInternalTeam(Team team) {
        tvTeamName.setText(team.getTeamName());
        tvTeamInfo.setText(team.getTeamInfo());
        ArrayList<ParseUser> lineup = team.getLineup();
        tvHiring.setText(String.format("Currntly hiring: %b", team.getHiring()));
        TeamPlayerAdapter adapter = new TeamPlayerAdapter(lineup, this);
        rvRoster.setAdapter(adapter);
        rvRoster.setLayoutManager(new LinearLayoutManager(this));
        if(!ParseUser.getCurrentUser().getObjectId().equals(team.getOwner().getObjectId()))
        {
            btnManage.setVisibility(View.GONE);
        }
        else
        {
            btnManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(TeamActivity.this, TeamManageActivity.class);
                    i.putExtra("team", team);
                    startActivity(i);
                }
            });
        }
        if(lineup.stream().anyMatch(t -> t.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())))
        {
            btnJoin.setVisibility(View.GONE);
        }
        else
        {
            btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    team.UpdateRequests(ParseUser.getCurrentUser().getObjectId());
                    team.saveInBackground();
                }
            });
        }
    }

    class LongRunningTask implements Callable<String> {
        private final String input;

        public LongRunningTask(String input) {
            this.input = input;
        }

        @Override
        public String call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.client.getPageByName(input);
        }
    }

    class MatchesTask implements Callable<JSONObject> {
        private final String input;

        public MatchesTask(String input) {
            this.input = input;
        }

        @Override
        public JSONObject call() throws IOException, JSONException {
            // Some long running task
            return MainActivity.client.getFullPage(input);
        }
    }

    private Team tryGetTeam(String teamName)
    {
        ParseQuery<Team> query =  ParseQuery.getQuery(Team.class);
        query.include("owner");
        query.include(Team.KEY_LINEUP);
        query.whereEqualTo("teamName", teamName);
        try {
            List<Team> res = query.find();
            if(res.size() == 0)
                return null;
            return res.get(0);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getMatches(Team t)
    {




        ParseQuery<TeamMatch> q2 = ParseQuery.getQuery(TeamMatch.class);
        q2.include("Team2");
        q2.include("Team1");
        q2.whereEqualTo("Team2", t);
        q2.findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                matches.addAll(objects);
                matchAdapter.notifyDataSetChanged();
            }
        });



        ParseQuery<TeamMatch> q3 = ParseQuery.getQuery(TeamMatch.class);
        q3.include("Team2");
        q3.include("Team1");
        q3.whereEqualTo("Team1", t);
        q3.findInBackground(new FindCallback<TeamMatch>() {
            @Override
            public void done(List<TeamMatch> objects, ParseException e) {
                matches.addAll(objects);
                matchAdapter.notifyDataSetChanged();
            }
        });

        Log.i("a","b");

    }
}