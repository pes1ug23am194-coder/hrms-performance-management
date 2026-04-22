package interfaces;

import models.Notification;
import java.util.List;

public interface INotificationRepository {
    void createNotification(Notification notification);
    List<Notification> getNotificationsForEmployee(String employeeId);
}
