package Blog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Blog.model.AdminPost;
import Blog.model.AdminResponse;
import Blog.model.Author;
import Blog.model.Dashboard;
import Blog.model.Post;
import Blog.model.PostResponse;
import Blog.model.Report;
import Blog.model.ReportPost;
import Blog.model.ReportResponse;
import Blog.model.User;
import Blog.model.UserReport;
import Blog.model.UserReportResponse;

@Service
public class AdminService {
    private PostService postService;
    private UserService userService;
    private LikeService likeService;
    private CommentService commentService;
    private FollowService followService;
    private ReportService reportService;

    public AdminService(
            PostService postService,
            UserService userService,
            LikeService likeService,
            CommentService commentService,
            FollowService followService,
            ReportService reportService) {
        this.postService = postService;
        this.userService = userService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.followService = followService;
        this.reportService = reportService;
    }

    public AdminResponse Admin() {
        AdminResponse Panel = new AdminResponse();
        List<Report> r = reportService.getReports(0);
        List<ReportResponse> rs = new ArrayList<>();
        for (Report sr : r) {
            ReportResponse report = new ReportResponse();
            report.setId(sr.getId());
            report.setReason(sr.getReason());
            Post p = sr.getPost();
            User u = userService.getUserByUsername(p.getAuthor().getUsername()).orElse(null);
            Author postauthor = new Author(u.getId(), u.getAvatar(), u.getUsername(), u.isStatus());
            ReportPost pr = new ReportPost(p.getId(), p.getTitle(), p.getContent(), postauthor);
            report.setPost(pr);
            User us = sr.getUser();
            Author a = new Author(u.getId(), us.getAvatar(), us.getUsername(), us.isStatus());
            report.setAuthor(a);
            report.setState(sr.isState());
            rs.add(report);
        }
        Panel.setReports(rs);
        List<AdminPost> posts = getAdminPostsList(0);
        Panel.setPosts(posts);
        Dashboard dashboard = getDashboard();
        Panel.setDashboard(dashboard);
        List<Author> users = userService.getAdminUsers(0);
        Panel.setUsers(users);
        return Panel;
    }

    public List<AdminPost> getAdminPostsList(long start) {
        List<Post> ps = postService.GetAdminPosts(start);
        List<AdminPost> posts = new ArrayList<>();
        for (Post p : ps) {
            // Get the user who authored the post
            User u = userService.getUserByUsername(p.getAuthor().getUsername()).orElse(null);
            Author postAuthor = new Author(u.getId(), u != null ? u.getAvatar() : null,
                    u != null ? u.getUsername() : null, u != null ? u.isStatus() : false);

            // Build the AdminPost object
            AdminPost ap = new AdminPost(
                    p.getId(),
                    p.getTitle(),
                    postAuthor,
                    p.getContent(),
                    commentService.CountPostLike(p.getId()),
                    likeService.CountPost(p.getId()),
                    p.isStatus());

            posts.add(ap);
        }

        return posts;
    }

    public boolean DeletePost(long id) {
        return postService.DeletePost(id);
    }

    public boolean Updatestatus(long id) {
        return postService.Changestatus(id);
    }

    public boolean UpdateUserStatus(long id) {
        return userService.togglestatus(id) == 1;
    }

    public boolean HandleReport(long id) {
        return reportService.HandleReport(id);
    }

    public Dashboard getDashboard() {
        Dashboard dashboard = new Dashboard();

        // USERS
        dashboard.setTotalusers(userService.countUsers());
        dashboard.setTotalactiveusers(userService.countActiveUsers());
        dashboard.setTotalsuspendedusers(userService.countSuspendedUsers());

        // POSTS
        dashboard.setTotalposts(postService.countPosts());
        dashboard.setTotalactiveposts(postService.countActivePosts());
        dashboard.setTotalsuspendedposts(postService.countSuspendedPosts());

        // COMMENTS
        dashboard.setTotalcomments(commentService.countComments());

        // REPORTS (Posts)
        dashboard.setTotalpendingreports(reportService.countPendingReports());
        dashboard.setTotalhandledreports(reportService.countHandledReports());
        dashboard.setTotalreports(dashboard.getTotalpendingreports() + dashboard.getTotalhandledreports());

        // USER REPORTS
        long pendingUserReports = reportService.countPendingUserReports();
        long handledUserReports = reportService.countHandledUserReports();
        dashboard.setTotalpendinguserreports(pendingUserReports);
        dashboard.setTotalhandleduserreports(handledUserReports);
        dashboard.setTotaluserreports(pendingUserReports + handledUserReports);

        List<User> latestUsers = userService.getLatestUsers();
        List<Author> latestAuthors = latestUsers.stream()
                .map(u -> new Author(u.getId(), u.getAvatar(), u.getUsername(), u.isStatus()))
                .collect(Collectors.toList());
        dashboard.setLatestusers(latestAuthors);
        User star = followService.mostfolloweduser();
        Author austar = new Author(star.getId(), star.getAvatar(), star.getUsername(), star.isStatus());
        dashboard.setStar(austar);
        long id = userService.getAdminid();
        PostResponse p = likeService.getMostLikedPost(id);
        dashboard.setMostlikedpost(p);
        return dashboard;
    }

    public List<ReportResponse> getAdminReportList(long start) {
        List<ReportResponse> rs = new ArrayList<>();
        if (start == 1) {
            return rs;
        }
        List<Report> r = reportService.getReports(start);
        for (Report sr : r) {
            ReportResponse report = new ReportResponse();
            report.setId(sr.getId());
            report.setReason(sr.getReason());
            Post p = sr.getPost();
            User u = userService.getUserByUsername(p.getAuthor().getUsername()).orElse(null);
            Author postauthor = new Author(u.getId(), u.getAvatar(), u.getUsername(), u.isStatus());
            ReportPost pr = new ReportPost(p.getId(), p.getTitle(), p.getContent(), postauthor);
            report.setPost(pr);
            User us = sr.getUser();
            Author a = new Author(u.getId(), us.getAvatar(), us.getUsername(), us.isStatus());
            report.setAuthor(a);
            report.setState(sr.isState());
            rs.add(report);
        }
        return rs;
    }

    public List<Author> getAdminUsersList(long start) {
        List<Author> rs = new ArrayList<>();
        if (start == 1) {
            return rs;
        }
        List<Author> users = userService.getAdminUsers(start);
        return users;
    }

    public boolean DeleteUser(long id) {
        return userService.DeleteUser(id);
    }

    /**
     * Get user reports for admin panel
     */
    public List<UserReportResponse> getAdminUserReportList(long start) {
        List<UserReportResponse> rs = new ArrayList<>();
        if (start == 1) {
            return rs;
        }
        List<UserReport> reports = reportService.getUserReports(start);
        for (UserReport ur : reports) {
            UserReportResponse response = new UserReportResponse();
            response.setId(ur.getId());
            response.setReason(ur.getReason());
            response.setState(ur.isState());

            // Reporter details
            User reporter = ur.getReporter();
            Author reporterAuthor = new Author(
                    reporter.getId(),
                    reporter.getAvatar(),
                    reporter.getUsername(),
                    reporter.isStatus());
            response.setReporter(reporterAuthor);

            // Reported user details
            User reportedUser = ur.getReportedUser();
            Author reportedAuthor = new Author(
                    reportedUser.getId(),
                    reportedUser.getAvatar(),
                    reportedUser.getUsername(),
                    reportedUser.isStatus());
            response.setReportedUser(reportedAuthor);

            rs.add(response);
        }
        return rs;
    }

    /**
     * Handle user report
     */
    public boolean handleUserReport(long reportId) {
        return reportService.handleUserReport(reportId);
    }

}
