package models;

import android.content.Intent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.example.sc2infoapp.MainActivity;
import com.example.sc2infoapp.R;

import java.text.ParseException;
import java.util.Date;

@Entity
public class ExternalMatchNotification extends Notification{


    public ExternalMatchNotification(){}
    public String opponents;
    public int bo;

    public ExternalMatchNotification(ExternalMatch match)
    {
        this.opponents = match.getOpponent();
        this.bo = match.getBo();
        this.title = String.format("Match update: %s", match.getOpponent());
        try {
            this.date = ExternalMatch.DATE_FORMATTER.parse(match.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.author = "";
        image = R.drawable.noun_versus;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public void setCallback(Runnable callback)
    {
        this.callback = callback;
    }
}
