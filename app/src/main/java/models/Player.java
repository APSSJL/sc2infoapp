package models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Player")
public class Player extends ParseObject {
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
}
