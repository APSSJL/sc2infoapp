package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import interfaces.IMatch;

public class ExternalMatch implements IMatch {
    private String opponent;
    private String time;
    private Integer bo;
    private String tournament;
    protected static final SimpleDateFormat DATE_PARRSER = new SimpleDateFormat("MMMM dd, yyyy - HH:mm z");
    protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-mm-dd hh:mm z");

    public ExternalMatch(String opponent, String time, Integer bo)
    {
        this.bo = bo;
        this.opponent = opponent;
        try {
            this.time = DATE_FORMATTER.format(DATE_PARRSER.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ExternalMatch(String opponent, String time, int bo, String tournament)
    {
        this.opponent = opponent;
        this.bo = bo;
        try {
            this.time = DATE_FORMATTER.format(DATE_PARRSER.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.tournament = tournament;
    }

    public String getTournament()
    {
        return this.tournament;
    }

    @Override
    public String getOpponent() {
        return opponent;
    }

    public Integer getBo()
    {
        return bo;
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
