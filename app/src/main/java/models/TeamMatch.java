package models;

import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import interfaces.IFollowable;
import interfaces.IMatch;
import interfaces.IPredictable;
import interfaces.IPublished;
import interfaces.IRateable;

import static com.parse.ParseUser.getCurrentUser;

@ParseClassName("TeamMatch")
public class TeamMatch extends ParseObject implements IMatch, IPredictable, IRateable, IFollowable, IPublished {
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

    @Override
    public int getResult1() {
        return getInt("T1Score");
    }

    @Override
    public int getResult2() {
        return getInt("T2Score");
    }

    public Pair<Integer, Integer> getDistribution()
    {
        return new Pair<>(getInt("t1PredictionVotes"), getInt("t2PredictionVotes"));
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
        increment("t1PredictionVotes");
        saveInBackground();
    }

    @Override
    public void predict2() {
        add("predicted", ParseUser.getCurrentUser().getObjectId());
        increment("t2PredictionVotes");
        saveInBackground();
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
    public boolean setFollow() {
        ParseUser user = getCurrentUser();
        if (this.getFollow()) {
            return false;
        } else {
            user.add("follows", getString("objectId"));
            user.saveInBackground();
            return true;
        }
    }

    @Override
    public boolean getFollow() {
        Object a = getCurrentUser().get("follows");
        if(a == null)
            return false;
        return ((ArrayList)a).contains(getString("objectID"));
    }

    @Override
    public int getPublishedType() {
        return MATCH_SUMMARY;
    }

    @Override
    public String getTitle() {
        return "Match";
    }

    @Override
    public String getContent() {
        return getDetails() + "\n" + getTime();
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public File getImage() {
        return null;
    }
}
