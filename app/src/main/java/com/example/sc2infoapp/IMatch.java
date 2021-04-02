package com.example.sc2infoapp;

import java.util.Date;

public interface IMatch {
    public static final int EXTERNAL = 0;
    public static final int INTERNAL = 1;
    public String getOpponent();
    public String getTime();
    public int getMatchType();
}
