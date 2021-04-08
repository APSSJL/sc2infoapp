package models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;

import interfaces.IPublished;

@ParseClassName("tournament")
public class Tournament extends ParseObject implements IPublished {
    public static final String KEY_RATING = "rating";
    public static final String KEY_NAME = "name";
    public static final String KEY_INTERNAL = "userCreated";

    public int getRating(){return getInt(KEY_RATING) ;}
    public String getName(){return getString(KEY_NAME);}
    public UserTournament getUserCreated(){return (UserTournament) getParseObject(KEY_INTERNAL) ;}



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
}
