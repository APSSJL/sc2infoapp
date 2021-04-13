package models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;

import interfaces.IFollowable;
import interfaces.IPublished;
import interfaces.IRateable;

import static com.parse.ParseUser.getCurrentUser;

@ParseClassName("tournament")
public class Tournament extends ParseObject implements IPublished, IFollowable, IRateable {
    public static final String KEY_RATING = "rating";
    public static final String KEY_NAME = "name";
    public static final String KEY_INTERNAL = "userCreated";

    public String getName(){return getString(KEY_NAME);}
    public UserTournament getUserCreated(){return (UserTournament) getParseObject(KEY_INTERNAL) ;}

    public int getRating(){
        Integer i = getInt(KEY_RATING);
        if(i == null) {
            return 0;
        } else {
            return getInt(KEY_RATING);
        }
    }

    @Override
    public int getPublishedType() {
        return TOURNAMENT;
    }

    @Override
    public String getTitle() {
        return getString(KEY_NAME);
    }

    @Override
    public String getContent() {
        UserTournament x = getUserCreated();
        if(x == null)
            return "";
        return x.getDescription();
    }

    @Override
    public String getAuthor() {
        UserTournament x = getUserCreated();
        if(x == null)
            return "";
        return x.getOrganizer().getUsername();
    }

    @Override
    public File getImage() {
        UserTournament x = getUserCreated();
        if(x == null)
            return null;
        ParseFile p = x.getOrganizer().getParseFile("pic");
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
            user.add("follows", "Tournament:"+ getName());
            user.saveInBackground();
            return true;
        }
    }

    @Override
    public boolean getFollow() {
        Object a = getCurrentUser().get("follows");
        if(a == null)
            return false;
        return ((ArrayList)a).contains("Tournament:"+ getName());
    }

    @Override
    public void setRate(double rate) {
        increment("ratingSum", rate);
        increment("ratingVotes", 1);
        put("rating", getRatingSum() / getRatingVotes());
        saveInBackground();
    }

    public void setUserCreated(UserTournament userTournament){put(KEY_INTERNAL,userTournament);}
    public void setTournName(String tournName){put(KEY_NAME,tournName);}

    @Override
    public double getRatingSum() {
        return getDouble("ratingSum");
    }

    @Override
    public double getRatingVotes() {
        return getDouble("ratingVotes");
    }

    public void setTitle(String title) { put(KEY_NAME, title);}

}
