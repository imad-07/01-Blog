package Blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Blog.model.UserReport;
import jakarta.transaction.Transactional;

/**
 * Repository for UserReport entity.
 */
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    /**
     * Get paginated user reports ordered by ID descending
     */
    List<UserReport> findTop20ByIdLessThanOrderByIdDesc(long id);

    /**
     * Get the latest user report
     */
    UserReport findTopByOrderByIdDesc();

    /**
     * Mark a user report as handled
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserReport r SET r.state = TRUE WHERE r.id = :id AND (r.state = FALSE OR r.state IS NULL)")
    int setHandledById(@Param("id") Long id);

    /**
     * Count pending user reports
     */
    long countByStateFalse();

    /**
     * Count handled user reports
     */
    long countByStateTrue();
}
