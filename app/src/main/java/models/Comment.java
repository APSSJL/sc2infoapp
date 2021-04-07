package models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";



    //GET
    public ParseUser getAuthor(){
        return getParseUser(KEY_AUTHOR);
    }

    public String getContent(){
        return getString(KEY_CONTENT);
    }

    //POST
    public void setAuthor(ParseUser author){
        put(KEY_AUTHOR, author);
    }

    public void setContent(String content){
        put(KEY_CONTENT, content);
    }



}
