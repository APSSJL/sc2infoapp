package interfaces;

import java.util.Date;

public interface IMatch {
    public static final int EXTERNAL = 0;
    public static final int INTERNAL = 1;
    public static final int TEAM = 2;
    public String getOpponent();
    public String getTime();
    public int getMatchType();
}
