package interfaces;

import java.io.File;
import java.util.Date;

public interface IPublished {
    int POST = 0;
    int TOURNAMENT = 1;
    int PLAYER_SUMMARY = 2;
    int USER_SUMMARY = 3;
    int TEAM_SUMMARY = 4;
    int MATCH_SUMMARY = 5;
    int NOTIFICATION = 6;
    public int getPublishedType();
    public Date getCreatedAt();
    String getTitle();
    String getContent();
    String getAuthor();
    File getImage();
}
