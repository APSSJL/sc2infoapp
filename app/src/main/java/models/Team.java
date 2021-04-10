package models;

import android.util.Log;
import android.widget.Toast;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import interfaces.IFollowable;
import interfaces.IPublished;

import static com.parse.ParseUser.getCurrentUser;

@ParseClassName("Team")
public class Team extends ParseObject implements IPublished, IFollowable {
    public Team(){};
    public static final String KEY_NAME = "teamName";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_INFO = "details";
    public static final String KEY_HIRING = "isHiring";
    public static final String KEY_LINEUP = "lineup";
    public static final String KEY_REQ = "joinRequests";
    public static final String KEY_RATING = "rating";

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
    public double getRating(){return  getDouble(KEY_RATING);};
    public void setRating(int i)
    {
            increment("ratingSum", i);
            increment("ratingVotes");
            add("rated", ParseUser.getCurrentUser().getObjectId());
            Log.i("TEAM", String.valueOf(getInt("ratingSum")));
            Log.i("TEAM", String.valueOf(getInt("ratingVotes")));

            put(KEY_RATING, getInt("ratingSum") / (float)getInt("ratingVotes"));
    }

    public void setOwner(ParseUser user)
    {
        put(KEY_OWNER, user);
    }

    public void setInfo(String info)
    {
        put(KEY_INFO, info);
    }

    public void setName(String name)
    {
        put(KEY_NAME, name);
    }

    public void setHiring(Boolean isHiring)
    {
        put(KEY_HIRING, isHiring);
    }

    public boolean getRated() {
        Object a = get("rated");
        if(a == null)
            return false;
        return  (((ArrayList)a).contains(ParseUser.getCurrentUser().getObjectId()));
    }

    public void addToLineup(ParseUser user)
    {
        getRelation("lineup").add(user);
        ArrayList<String> a = (ArrayList<String>) get("joinRequests");
        assert a != null;
        a.remove(user.getObjectId());
        put("joinRequests", a);
        saveInBackground();
        
    }

    public void removeRequest(String userId)
    {
        ArrayList<String> a = (ArrayList<String>) get("joinRequests");
        assert a != null;
        a.remove(userId);
        put("joinRequests", a);
        saveInBackground();
    }

    public ArrayList<String> getJoinRequests()
    {
        Object a = get("joinRequests");
        if(a == null)
            return new ArrayList<>();
        return  (ArrayList<String>)a;
    }

    @Override
    public int getPublishedType() {
        return TEAM_SUMMARY;
    }

    @Override
    public String getTitle() {
        return getTeamName();
    }

    @Override
    public String getContent() {
        return getTeamInfo();
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public File getImage() {
        return null;
    }

    @Override
    public boolean setFollow() {
        ParseUser user = getCurrentUser();
        if (this.getFollow()) {
            return false;
        } else {
            user.add("follows", "Team:"+getObjectId());
            user.saveInBackground();
            return true;
        }
    }

    @Override
    public boolean getFollow() {
        Object a = getCurrentUser().get("follows");
        if(a == null)
            return false;
        return ((ArrayList)a).contains("Team:"+getObjectId());
    }
}
