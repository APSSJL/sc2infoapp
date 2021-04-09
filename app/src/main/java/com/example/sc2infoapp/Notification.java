package com.example.sc2infoapp;

import android.app.Activity;

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

    public Notification(String content, String title, Date date, String author, Runnable callback) {
        this.content = content;
        this.title = title;
        this.date = date;
        this.author = author;
        this.callback = callback;
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

    @Override
    public File getImage() {
        return null;
    }
}
