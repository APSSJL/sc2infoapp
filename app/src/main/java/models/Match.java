package models;

import android.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import interfaces.IFollowable;
import interfaces.IMatch;
import interfaces.IPredictable;
import interfaces.IPublished;
import interfaces.IRateable;

import static com.parse.ParseUser.getCurrentUser;

@ParseClassName("PlayerMatch")
public class Match extends ParseObject implements IMatch, IPredictable, IRateable, IFollowable, IPublished {
    protected static final SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm z");
    public static final String KEY_DETAILS = "details";
    public static final String KEY_PLAYER1 = "Player1";
    public static final String KEY_PLAYER2 = "Player2";


    public String getTime(){
        Date d = getDate("time");
        if(d == null)
            return "";
        return dt.format(d);
    }
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
    public int getResult1() {
        return getInt("P1Score");
    }

    @Override
    public int getResult2() {
        return getInt("P2Score");
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
            user.add("follows", "Match:"+getString("objectId"));
            user.saveInBackground();
            return true;
        }
    }

    @Override
    public boolean getFollow() {
        Object a = getCurrentUser().get("follows");
        if(a == null)
            return false;
        return ((ArrayList)a).contains("Match:"+getString("objectId"));
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

    public ParseUser getWinner()
    {
        return getParseUser("winner");
    }

    public void setDetails(String string){put(KEY_DETAILS,string);}

    public void setPlayer1(ParseUser player1){put(KEY_PLAYER1,player1);}
    public void setPlayer2(ParseUser player2){put(KEY_PLAYER2,player2);}
}
