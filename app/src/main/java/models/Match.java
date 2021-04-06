package models;

import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import interfaces.IFollowable;
import interfaces.IMatch;
import interfaces.IPredictable;
import interfaces.IRateable;

import static com.parse.ParseUser.getCurrentUser;

@ParseClassName("PlayerMatch")
public class Match extends ParseObject implements IMatch, IPredictable, IRateable, IFollowable {
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


    public String getDetails()
    {
        return getString("details");
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
        increment("p1PredictionVotes");
        saveInBackground();
    }

    @Override
    public void predict2() {
        add("predicted", ParseUser.getCurrentUser().getObjectId());
        increment("p2PredictionVotes");
        saveInBackground();
    }

    public int getMatchType()
    {
        return INTERNAL;
    }

    @Override
    public void setRate(double rate) {
        increment("ratingSum", rate);
        increment("ratingVotes", 1);
        put("rating", getRatingSum() / getRatingVotes());
        saveInBackground();
    }

    @Override
    public double getRatingSum() {
        return getDouble("ratingSum");
    }

    @Override
    public double getRatingVotes() {
        return getDouble("ratingVotes");
    }

    @Override
    public void setFollow() {
        ParseUser user = getCurrentUser();
        user.add("follows", getString("objectID"));
        user.saveInBackground();
    }

    @Override
    public boolean getFollow() {
        Object a = getCurrentUser().get("follows");
        if(a == null)
            return false;
        return ((ArrayList)a).contains(getString("objectID"));
    }
}
