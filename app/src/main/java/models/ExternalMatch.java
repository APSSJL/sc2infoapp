package models;

import android.util.Log;

import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.ParseApplication;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import interfaces.IFollowable;
import interfaces.IMatch;
import interfaces.NotificationDao;

import static com.parse.ParseUser.getCurrentUser;

@Parcel
public class ExternalMatch implements IMatch, IFollowable {
    private  boolean treated;
    private Integer bo;
    private int result1;
    private int result2;
    private String opponent;
    private String time;
    private String tournament;
    public static final SimpleDateFormat DATE_PARRSER = new SimpleDateFormat("MMMM dd, yyyy - HH:mm z");
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm z");

    public ExternalMatch()
    {}

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

    public ExternalMatch(String opponent, String time)
    {
        this.bo = -1;
        this.opponent = opponent;
        this.time = time;
    }
    public ExternalMatch(String opponent, String time, String bo)
    {
        this.opponent = opponent;
        this.bo = Integer.parseInt(bo);
        this.time = time;
        this.tournament = "";
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

    public ExternalMatch(JSONObject match) {
        try {
            this.result1 = match.getInt("sca");
            this.result2 = match.getInt("scb");
            this.opponent = String.format("%s vs %s", match.getJSONObject("pla").getString("tag"), match.getJSONObject("plb").getString("tag"));
            this.time = match.getString("date");
            this.tournament = match.getJSONObject("eventobj").getString("fullname");
            this.treated = match.getBoolean("treated");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isTreated() {
        return treated;
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

    @Override
    public boolean setFollow() {
        Thread t = new Thread(() ->
        {
            MainActivity.notDao.insertNotification(new ExternalMatchNotification(this));
        });
        t.start();
        return true;
    }

    @Override
    public boolean getFollow() {
        Object a = getCurrentUser().get("follows");
        if(a == null)
            return false;
        return ((ArrayList)a).contains(String.format("External: "+ opponent));
    }

    @Override
    public int getResult1() {
        return result1;
    }

    @Override
    public int getResult2() {
        return result2;
    }

}
