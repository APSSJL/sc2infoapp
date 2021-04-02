package com.example.sc2infoapp;

import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("PlayerMatch")
public class Match extends ParseObject implements IMatch {
    public String getTime(){return getDate("time").toString();}
    public String getOpponent()
    {
        ParseUser p1 = ((ParseUser) getParseObject("Player1"));
        ParseUser p2 = ((ParseUser) getParseObject("Player2"));
        try {
            p1.fetchIfNeeded();
            p2.fetchIfNeeded();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return p1.getUsername() + " vs " + p2.getUsername();

    }

    public int getMatchType()
    {
        return INTERNAL;
    }
}
