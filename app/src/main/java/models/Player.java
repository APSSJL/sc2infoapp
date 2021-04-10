package models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;

import interfaces.IFollowable;
import interfaces.IPublished;

import static com.parse.ParseUser.getCurrentUser;

@ParseClassName("Player")
public class Player extends ParseObject implements IPublished , IFollowable {
    private static final String KEY_RATING = "rating";

    public  String getName()
    {
        return getString("name");
    }

    public double getRating(){return  getDouble(KEY_RATING);};
    public void setRating(int i)
    {
        increment("ratingSum", i);
        increment("ratingVotes");
        add("rated", ParseUser.getCurrentUser().getObjectId());
        Log.i("PLAYER", String.valueOf(getInt("ratingSum")));
        Log.i("PLAYER", String.valueOf(getInt("ratingVotes")));

        put(KEY_RATING, getInt("ratingSum") / (float)getInt("ratingVotes"));
    }

    public boolean getRated() {
        Object a = get("rated");
        if(a == null)
            return false;
        return ((ArrayList)a).contains(ParseUser.getCurrentUser().getObjectId());
    }

    @Override
    public int getPublishedType() {
        return PLAYER_SUMMARY;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public String getContent() {
        return "";
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public File getImage() {
        ParseFile p = getParseFile("picture");
        if(p == null)
            return null;
        try {
            return p.getFile();
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public boolean setFollow() {
        ParseUser user = getCurrentUser();
        if (this.getFollow()) {
            return false;
        } else {
            user.add("follows", "Player:"+getObjectId());
            user.saveInBackground();
            return true;
        }
    }

    @Override
    public boolean getFollow() {
        Object a = getCurrentUser().get("follows");
        if(a == null)
            return false;
        return ((ArrayList)a).contains("Player:"+getObjectId());
    }
}
