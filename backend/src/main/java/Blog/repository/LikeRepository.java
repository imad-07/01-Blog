package Blog.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Blog.model.Like;
import jakarta.transaction.Transactional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("SELECT l.post.id FROM Like l WHERE l.user.id = :userId")
    Set<Long> findLikedPostIdsByUserId(@Param("userId") long userId);

    @Transactional
    Like deleteByUserIdAndPostId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postid);

    Integer countByPostId(long postId);

    @Query(value = "SELECT postid FROM likes GROUP BY postid ORDER BY COUNT(*) DESC LIMIT 1", nativeQuery = true)
    Long findMostLikedPostId();
}
