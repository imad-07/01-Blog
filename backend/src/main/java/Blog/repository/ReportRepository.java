package Blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Blog.model.Post;
import Blog.model.Report;
import jakarta.transaction.Transactional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findTop20ByIdLessThanAndPostOrderByIdDesc(long id, Post post);

    Report findTop1ByPostOrderByIdDesc(Post post);

    List<Report> findTop20ByIdLessThanOrderByIdDesc(long id);

    Report findTop1ByOrderByIdDesc();

    Report findTopByOrderByIdDesc();
        @Modifying
    @Transactional
    @Query("UPDATE Report r SET r.state = TRUE WHERE r.id = :id AND (r.state = FALSE OR r.state IS NULL)")
    int SetHandledById(@Param("id") Long id);
    long countByStateTrue();
    long countByStateFalse();
}
