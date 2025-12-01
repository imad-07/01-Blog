package Blog.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Blog.model.Comment;
import jakarta.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT l.post.id FROM Comment l WHERE l.user.id = :userId")
    Set<Long> findCommentdPostIdsByUserId(@Param("userId") UUID userId);

    @Transactional
    Comment deleteByUserIdAndId(Long userId, Long Comment);

    Integer countByPostId(long id);

    List<Comment> findTop10ByPostIdAndIdLessThanOrderByIdDesc(Long postId, Long id);

    Comment findTopByOrderByIdDesc();

    long count();
}
