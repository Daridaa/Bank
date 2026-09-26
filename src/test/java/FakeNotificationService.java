import org.example.NotificationService;

public class FakeNotificationService implements NotificationService {

    private String lastMessage;
    private int notificationCount;

    @Override
    public void notify(String message) {
        lastMessage = message;
        notificationCount++;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getNotificationCount() {
        return notificationCount;
    }


    public void reset() {
        lastMessage = null;
        notificationCount = 0;
    }
}