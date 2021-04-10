package interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import models.ExternalMatch;
import models.ExternalMatchNotification;
import models.Notification;

@Dao
public interface NotificationDao {
    @Query("SELECT DISTINCT * FROM ExternalMatchNotification where date <= :time ORDER BY date DESC LIMIT 5 ")
    public List<ExternalMatchNotification> selectUpcoming(String time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertNotification(ExternalMatchNotification not);

    @Delete
    public void deleteUser(ExternalMatchNotification user);
}
