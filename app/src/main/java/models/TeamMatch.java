package models;

import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import interfaces.IMatch;
import interfaces.IPredictable;

@ParseClassName("TeamMatch")
public class TeamMatch extends ParseObject implements IMatch, IPredictable {
    protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-mm-dd hh:mm z");

    @Override
    public String getOpponent() {
        Team t1 = ((Team) getParseObject("Team1"));
        Team t2 = ((Team) getParseObject("Team2"));
        try {
            t1.fetchIfNeeded();
            t2.fetchIfNeeded();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t1.getTeamName() + " vs " + t2.getTeamName();
    }

    public String getTime(){return DATE_FORMATTER.format(getDate("time"));}

    @Override
    public int getMatchType() {
        return TEAM;
    }

    public Pair<Integer, Integer> getDistribution()
    {
        return new Pair<>(getInt("t1PredictionVotes"), getInt("t2PredictionVotes"));
    }


    public Boolean getPredicted()
    {
        Object a = get("predicted");
        if(a == null)
            return false;
        return ((ArrayList)a).contains(ParseUser.getCurrentUser().getObjectId());
    }

    @Override
    public void predict1() {
        add("predicted", ParseUser.getCurrentUser().getObjectId());
        increment("t1PredictionVotes");
        saveInBackground();
    }

    @Override
    public void predict2() {
        add("predicted", ParseUser.getCurrentUser().getObjectId());
        increment("t2PredictionVotes");
        saveInBackground();
    }

}
