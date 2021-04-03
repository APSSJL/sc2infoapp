package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("userTournament")
public class UserTournament extends ParseObject {
    public static final String KEY_AUTHOR = "organizer";
    public static final String KEY_DESCRIPTION = "description";

    public ParseUser getOrganizer(){return getParseUser(KEY_AUTHOR);}
    public String getDescription() {return getString(KEY_DESCRIPTION);}

}
