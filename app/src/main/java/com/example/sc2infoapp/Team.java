package com.example.sc2infoapp;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

@ParseClassName("Team")
public class Team extends ParseObject {
    public Team(){};
    public static final String KEY_NAME = "teamName";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_INFO = "details";
    public static final String KEY_HIRING = "isHiring";
    public static final String KEY_LINEUP = "lineup";
    public static final String KEY_REQ = "joinRequests";

    public String getTeamName(){return getString(KEY_NAME);}
    public ParseUser getOwner(){return getParseUser(KEY_OWNER);}
    public String getTeamInfo(){return getString(KEY_INFO);}
    public boolean getHiring(){return getBoolean(KEY_HIRING);}
    public ArrayList<ParseUser> getLineup() {
        try {
            fetchIfNeeded();
            ParseRelation<ParseUser> x =  getRelation(KEY_LINEUP);
            return (ArrayList<ParseUser>) x.getQuery().find();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void UpdateRequests(String user){add(KEY_REQ, user);}
}
