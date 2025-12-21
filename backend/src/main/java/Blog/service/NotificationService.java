package Blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import Blog.model.Author;
import Blog.model.Notification;
import Blog.model.NotificationResponse;
import Blog.model.NotificationType;
import Blog.model.User;
import Blog.repository.NotificationRepository;

@Service
public class NotificationService {
    private NotificationRepository notificationRepository;
    private UserService userService;

    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public List<NotificationResponse> Getnotification(String usr, long start) {
        User user = userService.getUserByUsername(usr).orElse(null);
        if (start == 1 || user == null) {
            return new ArrayList<>();
        }
        if (start == 0) {
            Notification p = notificationRepository.findTopByRecieverOrderByIdDesc(user);
            if (p == null) {
                return new ArrayList<>();
            }
            start = p.getId() + 1;
        }
        List<Notification> notifs = notificationRepository.findTop5ByRecieverAndIdLessThanOrderByIdDesc(user, start);
        List<NotificationResponse> resp = new ArrayList<>();
        for (Notification notification : notifs) {
            NotificationResponse noresp = new NotificationResponse();
            User us = notification.getSender();
            Author u = new Author(us.getId(), us.getAvatar(), us.getUsername(),
                    us.isStatus(), us.getRole());
            noresp.setSender(u);
            noresp.setContent(notification.getContent());
            noresp.setSeen(notification.isSeen());
            noresp.setCreatedAt(notification.getCreatedAt());
            noresp.setId(notification.getId());
            resp.add(noresp);
        }
        return resp;
    }

    public void save(User sender, User reciever, NotificationType type) {
        if (sender.getId().equals(reciever.getId())) {
            return;
        }
        Notification n = new Notification();
        n.setSender(sender);
        n.setReciever(reciever);
        n.setContent(type.getDescription());
        notificationRepository.save(n);
    }

    public boolean toggleseen(String username, long id) {
        Notification n = notificationRepository.getReferenceById(id);
        if (n.getReciever().getUsername().equals(username)) {
            return notificationRepository.updateseenById(id) == 1;
        }
        return false;
    }

    public boolean allleseen(String username) {
        try {
            notificationRepository.updateseenByUserId(username);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean delete(String username, long id) {
        return notificationRepository.deleteByIdAndRecieverId(id, username) == 1;
    }

    public boolean deleteAll(String username) {
        try {
            notificationRepository.deleteByRecieverUsername(username);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public void notifysubscribers(long userid, NotificationType ntype) {
        this.notificationRepository.notifyFollowers(userid, ntype.getDescription());
    }
}
