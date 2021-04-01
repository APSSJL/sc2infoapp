package com.example.sc2infoapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {

    TextView tvTeamName;
    TextView tvTeamInfo;
    TextView tvHiring;
    Button btnJoin;
    Button btnManage;
    RecyclerView rvRoster;
    String teamName;

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

        teamName = getIntent().getStringExtra("teamName");
        Team t = tryGetTeam(teamName);
        if(t != null)
        {
            setupInternalTeam(t);
        }
        else
        {
            //getInfoFromApi();
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
        Log.i("fuck", ParseUser.getCurrentUser().getUsername());
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