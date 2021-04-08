package com.example.sc2infoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import models.Team;
import models.UserInfo;

public class CreateTeamActivity extends AppCompatActivity {

    private static final int MAX_TEAMNAME_LENGTH = 50;
    EditText edTeamName;
    EditText edTeamInfo;
    CheckBox checkboxIsHiring;
    Button btnCreate;
    TextView tvCharCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);


        edTeamName = findViewById(R.id.edTeamName);
        edTeamInfo = findViewById(R.id.edTeamInfo);
        checkboxIsHiring = findViewById(R.id.checkboxIsHiring);
        btnCreate = findViewById(R.id.btnCreate);
        tvCharCounter = findViewById(R.id.tvCharCount);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edTeamName.getText().toString();
                if(name.length() == 0)
                {
                    Toast.makeText(CreateTeamActivity.this, "Can't create team with empty name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(name.length() > 50)
                {
                    Toast.makeText(CreateTeamActivity.this, "Team name length too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseQuery<Team> query = new ParseQuery<Team>(Team.class);
                query.whereEqualTo(Team.KEY_NAME, name);
                try {
                    if(query.find().size() != 0)
                    {
                        Toast.makeText(CreateTeamActivity.this, "Team already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Team team = new Team();
                team.setOwner(ParseUser.getCurrentUser());
                team.setName(name);
                team.setInfo(edTeamInfo.getText().toString());
                team.setHiring(checkboxIsHiring.isChecked());
                team.getRelation("lineup").add(ParseUser.getCurrentUser());
                team.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null)
                        {
                            Toast.makeText(CreateTeamActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(CreateTeamActivity.this, "Team created!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                UserInfo info = (UserInfo) ParseUser.getCurrentUser().get("Additional");
                if(info != null)
                {
                    info.put("team", team);
                    team.saveInBackground();
                }
            }
        });

        edTeamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                tvCharCounter.setText(String.format("%d/50", edTeamName.length()));
                if(edTeamName.length() <= MAX_TEAMNAME_LENGTH)
                {
                    btnCreate.setEnabled(true);
                    btnCreate.setBackgroundResource(R.color.light_primary_color);
                }
                else
                {
                    btnCreate.setEnabled(false);
                    btnCreate.setBackgroundResource(R.color.primary_color);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}