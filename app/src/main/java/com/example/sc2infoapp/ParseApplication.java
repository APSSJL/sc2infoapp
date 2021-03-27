package com.example.sc2infoapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(
                new Parse.Configuration.Builder(this)
                .applicationId("pkQlx3z7vADICB26cJFrsw2Qy8AA84MWekWWFdll")
                .clientKey("egS1PexyMWxafvxl3SpdXiSnqfv1etx8QbpBGtCq")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
