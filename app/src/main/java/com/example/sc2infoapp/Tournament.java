package com.example.sc2infoapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("userTournament")
public class Tournament extends ParseObject implements IPublished {
    public static final String KEY_AUTHOR = "organizer";

    @Override
    public int getPublishedType() {
        return 1;
    }
}
