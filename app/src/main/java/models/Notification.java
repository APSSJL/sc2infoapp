package models;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

import interfaces.IPublished;

@Entity
public class Notification implements IPublished {

    public Notification(){}

    @Override
    public int getPublishedType() {
        return NOTIFICATION;
    }

    @ColumnInfo
    @PrimaryKey(autoGenerate=true)
    public Long id;

    @ColumnInfo
    public String content;
    @ColumnInfo
    public String title;
    @ColumnInfo
    public Date date;
    @ColumnInfo
    public String author;
    @Ignore
    public Runnable callback;
    @ColumnInfo
    public int image;

    public Notification(String content, String title, Date date, String author, Runnable callback, int d) {
        this.content = content;
        this.title = title;
        this.date = date;
        this.author = author;
        this.callback = callback;
        image = d;
    }

    public void Enforce()
    {
        try {
            callback.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Date getCreatedAt() {
        return date;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    public int getResource()
    {
        return image;
    }

    @Override
    public File getImage() {
        return null;
    }
}
