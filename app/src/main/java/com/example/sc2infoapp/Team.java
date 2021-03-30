package com.example.sc2infoapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Team")
public class Team extends ParseObject {
    public Team(){};

    public String getTeamName(){return getString("teamName");}
    public ParseUser getOwner(){return getParseUser("owner");}
}
