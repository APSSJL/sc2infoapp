package interfaces;

import java.io.File;
import java.util.Date;

public interface IPublished {
    public static final int POST = 0;
    public static final int TOURNAMENT = 1;
    int PLAYER_SUMMARY = 2;
    int USER_SUMMARY = 3;
    int TEAM_SUMMARY = 4;
    int MATCH_SUMMARY = 4;
    public int getPublishedType();
    public Date getCreatedAt();
    String getTitle();
    String getContent();
    String getAuthor();
    File getImage();
}
