package models;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import interfaces.NotificationDao;

@Database(entities = {ExternalMatchNotification.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class MatchesDatabase extends RoomDatabase {
    public abstract NotificationDao notDao();
    public static final String NAME = "MATCHESDB";
}
