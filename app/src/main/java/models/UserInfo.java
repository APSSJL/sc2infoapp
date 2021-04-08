package models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;

import interfaces.IPublished;

@ParseClassName("UserInfo")
public class UserInfo extends ParseObject implements IPublished {
    ParseUser user;
    public UserInfo(ParseUser user) {
        this.user = user;
    }

    public UserInfo() {
    }

    @Override
    public int getPublishedType() {
        return USER_SUMMARY;
    }

    @Override
    public String getTitle() {
        return user.getUsername();
    }

    @Override
    public String getContent() {
        return user.getString("userInfo");
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public File getImage() {
        ParseFile p = user.getParseFile("pic");
        if(p == null)
            return null;
        try {
            return p.getFile();
        } catch (ParseException e) {
            return null;
        }
    }
}
