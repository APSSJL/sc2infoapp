package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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

        teamName = getIntent().getStringExtra("teamName");
        Team t = tryGetTeam(teamName);

        if(t != null)
        {
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

        if(t != null && t.getOwner() != null)
        {
            setupInternalTeam(t);
        }
        else
        {
            getInfoFromApi();
        }

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
        if(ParseUser.getCurrentUser() != team.getOwner())
        {
            btnManage.setVisibility(View.GONE);
        }
        else
        {
            // TODO : redirect to manage team screen
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
}