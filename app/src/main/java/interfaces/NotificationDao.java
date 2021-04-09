package interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import models.ExternalMatch;
import models.ExternalMatchNotification;
import models.Notification;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM ExternalMatchNotification where date <= :time ORDER BY date DESC LIMIT 5 ")
    public ExternalMatchNotification selectUpcoming(String time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertNotification(ExternalMatchNotification not);

    @Delete
    public void deleteUser(ExternalMatchNotification user);
}
