package Blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Blog.model.Notification;
import Blog.model.User;
import jakarta.transaction.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop5ByRecieverAndIdLessThanOrderByIdDesc(User Reciever, long lastNotifId);

    Notification findTopByRecieverOrderByIdDesc(User user);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.seen = CASE WHEN n.seen = true THEN false ELSE true END WHERE n.id = :id")
    int updateseenById(@Param("id") long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.id = :notifId AND n.reciever.username = :reciever")
    int deleteByIdAndRecieverId(@Param("notifId") long notifId, @Param("reciever") String recieverId);

    @Modifying
    @Transactional
    void deleteByRecieverUsername(@Param("reciever") String recieverId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.seen = true WHERE n.reciever.username = :reciever")
    int updateseenByUserId(@Param("reciever") String recieverId);
}
