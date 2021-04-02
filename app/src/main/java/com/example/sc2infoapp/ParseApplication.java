package com.example.sc2infoapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Team.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(UserTournament.class);
        ParseObject.registerSubclass(Tournament.class);
        ParseObject.registerSubclass(Player.class);

        Parse.initialize(
                new Parse.Configuration.Builder(this)
                .applicationId("pkQlx3z7vADICB26cJFrsw2Qy8AA84MWekWWFdll")
                .clientKey(BuildConfig.CLIENT_KEY)
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
