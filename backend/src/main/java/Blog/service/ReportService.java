package Blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.model.ReportReason;
import Blog.model.Post;
import Blog.model.Report;
import Blog.model.User;
import Blog.model.UserReport;
import Blog.repository.ReportRepository;
import Blog.repository.UserReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserReportRepository userReportRepository;
    private final UserService userservice;
    private final PostService postservice;

    public ReportService(ReportRepository reportRepository, UserReportRepository userReportRepository,
            UserService userservice, PostService postservice) {
        this.reportRepository = reportRepository;
        this.userReportRepository = userReportRepository;
        this.userservice = userservice;
        this.postservice = postservice;
    }

    public void Report(UserDetails usr, long postid, String Reson) {
        User user = userservice.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            throw new Blog.exception.NotFoundException("User not found");
        }
        Post post = postservice.postrepository.findById(postid).orElse(null);
        if (post == null || !post.isStatus()) {
            throw new Blog.exception.NotFoundException("Post not found");
        }
        ReportReason reason = toReportError(Reson);
        if (reason == null) {
            throw new Blog.exception.BadRequestException("Invalid report reason");
        }

        Report report = new Report();
        report.setPost(post);
        report.setUser(user);
        report.setReason(reason);

        try {
            reportRepository.save(report);
        } catch (Exception e) {
            // Catch database constraint violation for duplicate reports
            throw new Blog.exception.ConflictException("Report already exists");
        }
    }

    public static ReportReason toReportError(String value) {
        if (value == null)
            return null;

        try {
            return ReportReason.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<Report> getReports(long start) {
        if (start == 1) {
            return new ArrayList<>();
        }
        if (start == 0) {
            Report p = reportRepository.findTopByOrderByIdDesc();
            if (p == null) {
                return new ArrayList<>();
            }
            start = p.getId() + 1;
        }
        List<Report> r = reportRepository.findTop20ByIdLessThanOrderByIdDesc(start);
        return r;
    }

    public boolean HandleReport(long id) {
        Integer x = reportRepository.SetHandledById(id);
        return x == 1 ? true : false;
    }

    public long countPendingReports() {
        return reportRepository.countByStateFalse();
    }

    public long countHandledReports() {
        return reportRepository.countByStateTrue();
    }

    /**
     * Report a user for inappropriate behavior
     */
    public void reportUser(UserDetails reporter, long userId, String reasonStr) {
        User reporterUser = userservice.getUserByUsername(reporter.getUsername()).orElse(null);
        if (reporterUser == null) {
            throw new Blog.exception.NotFoundException("Reporter user not found");
        }

        User reportedUser = userservice.getUserById(userId).orElse(null);
        if (reportedUser == null) {
            throw new Blog.exception.NotFoundException("Reported user not found");
        }

        // Prevent self-reporting
        if (reporterUser.getId() == reportedUser.getId()) {
            throw new Blog.exception.BadRequestException("Cannot report yourself");
        }

        ReportReason reason = toReportError(reasonStr);
        if (reason == null) {
            throw new Blog.exception.BadRequestException("Invalid report reason");
        }

        UserReport report = new UserReport();
        report.setReporter(reporterUser);
        report.setReportedUser(reportedUser);
        report.setReason(reason);
        report.setState(false); // pending

        try {
            userReportRepository.save(report);
        } catch (Exception e) {
            // Catch database constraint violation for duplicate reports
            throw new Blog.exception.ConflictException("Report already exists");
        }
    }

    /**
     * Get paginated user reports for admin
     */
    public List<UserReport> getUserReports(long startId) {
        if (startId == 1) {
            return new ArrayList<>();
        }
        if (startId == 0) {
            UserReport p = userReportRepository.findTopByOrderByIdDesc();
            if (p == null) {
                return new ArrayList<>();
            }
            startId = p.getId() + 1;
        }
        List<UserReport> r = userReportRepository.findTop20ByIdLessThanOrderByIdDesc(startId);
        return r;
    }

    /**
     * Mark user report as handled
     */
    public boolean handleUserReport(long id) {
        Integer x = userReportRepository.setHandledById(id);
        return x == 1;
    }

    /**
     * Count pending user reports
     */
    public long countPendingUserReports() {
        return userReportRepository.countByStateFalse();
    }

    /**
     * Count handled user reports
     */
    public long countHandledUserReports() {
        return userReportRepository.countByStateTrue();
    }
}
