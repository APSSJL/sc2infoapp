package models;

import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import interfaces.IMatch;

@ParseClassName("PlayerMatch")
public class Match extends ParseObject implements IMatch {
    protected static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm z");
    public String getTime(){return dt.format(getDate("time"));}
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

    public Pair<Integer, Integer> getDistribution()
    {
        return new Pair<>(getInt("p1PredictionVotes"), getInt("p2PredictionVotes"));
    }


    public Boolean getPredicted()
    {
        Object a = get("predicted");
        if(a == null)
            return false;
        return ((ArrayList)a).contains(ParseUser.getCurrentUser().getObjectId());
    }
    public int getMatchType()
    {
        return INTERNAL;
    }
}
