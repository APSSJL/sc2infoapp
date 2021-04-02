package com.example.sc2infoapp;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Player")
public class Player extends ParseObject {
    private static final String KEY_RATING = "rating";

    public double getRating(){return  getDouble(KEY_RATING);};
    public void setRating(int i)
    {
        increment("ratingSum", i);
        increment("ratingVotes");
        put("rated", true);
        Log.i("PLAYER", String.valueOf(getInt("ratingSum")));
        Log.i("PLAYER", String.valueOf(getInt("ratingVotes")));

        put(KEY_RATING, getInt("ratingSum") / (float)getInt("ratingVotes"));
    }
}
