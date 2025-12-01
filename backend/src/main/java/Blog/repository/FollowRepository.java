package Blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import Blog.model.Follow;
import Blog.model.User;
import jakarta.transaction.Transactional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    // Followers: get top 100 followers of a user before a certain follow record ID
    List<Follow> findTop100ByFollowedAndIdLessThanOrderByIdDesc(User followed, long lastFollowId);

    // Following: get top 100 users followed by a user before a certain follow
    // record ID
    List<Follow> findTop100ByFollowerAndIdLessThanOrderByIdDesc(User follower, long lastFollowId);

    // Get the latest follow record where the user is the follower
    Follow findTop1ByFollowerOrderByIdDesc(User follower);

    // Get the latest follow record where the user is being followed
    Follow findTop1ByFollowedOrderByIdDesc(User followed);

    boolean existsByFollowerAndFollowed(User follower, User followed);

    @Transactional
    void deleteByFollowerAndFollowed(User follower, User followed);

    @Query(value = "SELECT followedid FROM follows GROUP BY followedid ORDER BY COUNT(*) DESC LIMIT 1", nativeQuery = true)
    long findMostFollowedUserId();
}
