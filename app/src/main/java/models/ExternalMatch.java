package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import interfaces.IMatch;

public class ExternalMatch implements IMatch {
    private String opponent;
    private String time;
    protected static final SimpleDateFormat DATE_PARRSER = new SimpleDateFormat("MMMM dd, yyyy - HH:mm z");
    protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-mm-dd hh:mm z");

    public ExternalMatch(String opponent, String time)
    {
        this.opponent = opponent;
        try {
            this.time = DATE_FORMATTER.format(DATE_PARRSER.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
