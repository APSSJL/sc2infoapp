package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import interfaces.IPublished;

@ParseClassName("tournament")
public class Tournament extends ParseObject implements IPublished {
    public static final String KEY_RATING = "rating";
    public static final String KEY_NAME = "name";
    public static final String KEY_INTERNAL = "userCreated";

    public int getRating(){return getInt(KEY_RATING) ;}
    public String getName(){return getString(KEY_NAME) ;}
    public UserTournament getUserCreated(){return (UserTournament) getParseObject(KEY_INTERNAL) ;}

    @Override
    public int getPublishedType() {
        return TOURNAMENT;
    }
}
