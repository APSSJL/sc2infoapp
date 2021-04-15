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
    @Query("SELECT DISTINCT title,date,image,opponents,bo,username FROM ExternalMatchNotification where date <= :time AND username = :username  ORDER BY date DESC LIMIT 5 ")
    public List<ExternalMatchNotification> selectUpcoming(String time, String username);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insertNotification(ExternalMatchNotification not);

    @Delete
    public void deleteUser(ExternalMatchNotification user);
}
