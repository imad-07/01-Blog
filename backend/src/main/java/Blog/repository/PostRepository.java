package Blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Blog.model.Post;
import Blog.model.User;
import jakarta.transaction.Transactional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findTop20ByIdLessThanOrderByIdDesc(long id);

    List<Post> findTop20ByIdLessThanAndStatusTrueOrderByIdDesc(long id);

    @Query(value = """
            SELECT p.*
            FROM posts p
            WHERE p.status = true
              AND p.id < :startId
              AND p.userid IN (
                  SELECT u.id
                  FROM users u
                  WHERE u.id IN (
                      SELECT f.followedid
                      FROM follows f
                      WHERE f.followerid = :userId
                  )
              )
            ORDER BY p.id DESC
            LIMIT 20
                        """, nativeQuery = true)
    List<Post> findFeedPage(
            @Param("userId") long userId,
            @Param("startId") long startId);

    List<Post> findTop20ByIdLessThanAndAuthorOrderByIdDesc(Long id, User author);

    List<Post> findTop20ByIdLessThanAndAuthorAndStatusTrueOrderByIdDesc(Long id, User author);

    Post findTopByOrderByIdDesc();

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.title = :title WHERE p.id = :id")
    int updateTitleById(@Param("id") Long id, @Param("title") String title);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.media = :media WHERE p.id = :id")
    int updateMediaById(@Param("id") Long id, @Param("media") String media);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.content = :content WHERE p.id = :id")
    int updateContentById(@Param("id") Long id, @Param("content") String content);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.status = CASE WHEN p.status = true THEN false ELSE true END WHERE p.id = :id")
    int updatestatusById(@Param("id") Long id);

    @Query("SELECT p.author FROM Post p WHERE p.id = ?1")
    String getAuthId(long id);

    @Query("SELECT p.status FROM Post p WHERE p.id = ?1")
    boolean getStatus(long id);

    @Query("SELECT p.media FROM Post p WHERE p.id = ?1")
    String getMedia(long id);

    Optional<Post> findById(long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Post p WHERE p.id = :postId")
    int deletePostById(@Param("postId") Long postId);

    long count();

    long countByStatusTrue();

    long countByStatusFalse();

}
