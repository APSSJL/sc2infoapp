package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import adapters.RequestAdapter;
import models.Team;

public class TeamManageActivity extends AppCompatActivity {

    EditText edTeamName;
    EditText edTeamInfo;
    CheckBox checkboxIsHiring;
    Button btnSave;
    TextView tvCharCounter;
    RecyclerView rvRequests;
    Team team;
    RequestAdapter adapter;
    ArrayList<ParseUser> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_manage);


        edTeamName = findViewById(R.id.edTeamName);
        edTeamInfo = findViewById(R.id.edTeamInfo);
        checkboxIsHiring = findViewById(R.id.checkboxIsHiring);
        btnSave = findViewById(R.id.btnSave);
        tvCharCounter = findViewById(R.id.tvCharCount);
        rvRequests = findViewById(R.id.rvRequests);

        requests = new ArrayList<>();



        team = getIntent().getParcelableExtra("team");
        if(team == null)
            finish();

        adapter = new RequestAdapter(this, requests, team, this);

        edTeamName.setText(team.getTeamName());
        edTeamInfo.setText(team.getTeamInfo());
        checkboxIsHiring.setChecked(team.getHiring());
        tvCharCounter.setText(String.format("%d/50",team.getTeamName().length()));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edTeamName.getText().toString();
                if(name.length() == 0)
                {
                    Toast.makeText(TeamManageActivity.this, "Can't create team with empty name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(name.length() > 50)
                {
                    Toast.makeText(TeamManageActivity.this, "Team name length too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseQuery<Team> query = new ParseQuery<Team>(Team.class);
                query.whereEqualTo(Team.KEY_NAME, name);
                try {
                    if(query.find().size() != 0)
                    {
                        Toast.makeText(TeamManageActivity.this, "Team already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                team.setOwner(ParseUser.getCurrentUser());
                team.setName(name);
                team.setInfo(edTeamInfo.getText().toString());
                team.setHiring(checkboxIsHiring.isChecked());
                team.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null)
                        {
                            Toast.makeText(TeamManageActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(TeamManageActivity.this, "Team saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(adapter);

        getRequests();
    }


    public void getRequests()
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", team.getJoinRequests());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                requests.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }
}