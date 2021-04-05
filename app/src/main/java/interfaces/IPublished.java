package interfaces;

import java.util.Date;

public interface IPublished {
    public static final int POST = 0;
    public static final int TOURNAMENT = 1;
    public int getPublishedType();
    public Date getCreatedAt();
}
