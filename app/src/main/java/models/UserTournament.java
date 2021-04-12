package models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("userTournament")
public class UserTournament extends Tournament {
    public static final String KEY_AUTHOR = "organizer";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ISTEAM = "isTeam";
    public static final String KEY_MATCHES = "matches";

    public boolean isTeam() {return getBoolean(KEY_ISTEAM);}
    public ParseUser getOrganizer(){return getParseUser(KEY_AUTHOR);}
    public String getDescription() {return getString(KEY_DESCRIPTION);}
    public ArrayList<String> getMatches()
    {
        JSONArray x = getJSONArray(KEY_MATCHES);

        ArrayList<String> res = new ArrayList<>();
        if(x == null)
            return res;
        for(int i = 0; i < x.length(); i++)
        {
            try {
                res.add(x.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  res;
    }

}
