package com.example.sc2infoapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

import interfaces.IPublished;

public class Notification implements IPublished {
    @Override
    public int getPublishedType() {
        return NOTIFICATION;
    }

    private String content;
    private String title;
    private Date date;
   private String author;
   private Runnable callback;
   private int image;

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
