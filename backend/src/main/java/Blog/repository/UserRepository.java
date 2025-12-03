package Blog.repository;

import Blog.model.User;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    @Query("SELECT u.avatar FROM User u WHERE u.username = :username")
    String getAvatar(@Param("username") String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT u.id FROM User u WHERE u.role = 'ADMIN'")
    long findAdminUserId();

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = CASE WHEN u.status = true THEN false ELSE true END WHERE u.id = :id")
    int updatestatusById(@Param("id") Long id);

    long count();

    long countByStatusTrue();

    long countByStatusFalse();

    User findTopByOrderByIdDesc();

    List<User> findTop20ByIdLessThanOrderByIdDesc(long id);

    List<User> findTop3ByOrderByCreatedAtDesc();

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.id = :userid")
    int deleteUserById(@Param("userid") Long userid);
}
