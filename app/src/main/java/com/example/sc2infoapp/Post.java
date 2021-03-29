package com.example.sc2infoapp;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject implements IPublished {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_TAGS = "tags";

    public ParseUser getAuthor() {return getParseUser(KEY_AUTHOR);}
    public String getTitle() {return getString(KEY_TITLE);}
    public String getContent() {return getString(KEY_CONTENT);}
    public String getCategory() {return getString(KEY_CATEGORY);}
    public ArrayList<String> getTags(){return (ArrayList<String>) get(KEY_TAGS);}

    @Override
    public int getPublishedType() {
        return POST;
    }
}
