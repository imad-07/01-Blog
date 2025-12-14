package Blog.model;

/**
 * DTO for returning user report data to admin dashboard.
 */
public class UserReportResponse {
    private long id;
    private Author reporter;
    private Author reportedUser;
    private ReportReason reason;
    private boolean state;

    public UserReportResponse() {
    }

    public UserReportResponse(long id, Author reporter, Author reportedUser, ReportReason reason, boolean state) {
        this.id = id;
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reason = reason;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Author getReporter() {
        return reporter;
    }

    public void setReporter(Author reporter) {
        this.reporter = reporter;
    }

    public Author getReportedUser() {
        return reportedUser;
    }

    public void setReportedUser(Author reportedUser) {
        this.reportedUser = reportedUser;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
