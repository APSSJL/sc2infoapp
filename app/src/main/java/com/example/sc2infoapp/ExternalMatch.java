package com.example.sc2infoapp;

import java.util.Date;

public class ExternalMatch implements IMatch{
    private String opponent;
    private String time;

    public ExternalMatch(String opponent, String time)
    {
        this.opponent = opponent;
        this.time = time;
    }

    @Override
    public String getOpponent() {
        return opponent;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public int getMatchType() {
        return EXTERNAL;
    }
}
