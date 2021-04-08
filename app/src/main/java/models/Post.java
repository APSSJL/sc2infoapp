package models;

import interfaces.IPublished;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@ParseClassName("Post")
public class Post extends ParseObject implements IPublished {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_TAGS = "tags";

    public ParseUser getUser() {return getParseUser(KEY_AUTHOR);}
    public String getAuthor() {return getParseUser(KEY_AUTHOR).getUsername();}

    @Override
    public File getImage() {
        ParseFile p = getUser().getParseFile("pic");
        if(p == null)
            return null;
        try {
            return p.getFile();
        } catch (ParseException e) {
            return null;
        }
    }

    public String getTitle() {return getString(KEY_TITLE);}
    public String getContent() {return getString(KEY_CONTENT);}
    public String getCategory() {return getString(KEY_CATEGORY);}
    public ArrayList<String> getTags(){return (ArrayList<String>) get(KEY_TAGS);}

    public void setAuthor(ParseUser author) { put(KEY_AUTHOR, author);}
    public void setTitle(String title) { put(KEY_TITLE, title);}
    public void setContent(String content) { put(KEY_CONTENT, content);}
    public void setCategory(String category) { put(KEY_CATEGORY, category);}
    public void setTags(String tags) {
        String temp = tags.trim();
        ArrayList<String> tagsList = new ArrayList<String>(Arrays.asList(temp.split(",")));
        put(KEY_TAGS, tagsList);
    }

    @Override
    public int getPublishedType() {
        return POST;
    }
}
