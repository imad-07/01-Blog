package Blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.model.Errors;
import Blog.model.Errors.ReportReason;
import Blog.model.Post;
import Blog.model.Report;
import Blog.model.User;
import Blog.repository.ReportRepository;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userservice;
    private final PostService postservice;

    public ReportService(ReportRepository reportRepository, UserService userservice, PostService postservice) {
        this.reportRepository = reportRepository;
        this.userservice = userservice;
        this.postservice = postservice;
    }

    public Errors.ReportError Report(UserDetails usr, long postid, String Reson) {
        User user = userservice.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            return Errors.ReportError.InvalidUser;
        }
        Post post = postservice.postrepository.findById(postid).orElse(null);
        if (post == null) {
            return Errors.ReportError.InvalidPost;
        }
        ReportReason reason = toReportError(Reson);
        if (reason == null) {
            return Errors.ReportError.InvalidReason;
        }
        Report report = new Report();
        report.setPost(post);
        report.setUser(user);
        report.setReason(reason);
        try {
            reportRepository.save(report);
        } catch (Exception e) {
            return Errors.ReportError.Existing;
        }

        return Errors.ReportError.Success;
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
        System.out.println(x);
        return x == 1 ? true : false;
    }

    public long countPendingReports() {
        return reportRepository.countByStateFalse();
    }

    public long countHandledReports() {
        return reportRepository.countByStateTrue();
    }
}
