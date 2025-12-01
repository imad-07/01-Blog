package Blog.model;

import java.util.List;

public class AdminResponse {
    private List<ReportResponse> reports;
    private List<AdminPost> posts;
    private List<Author> users;
    private Dashboard dashboard;
    private long activeuserscount;
    private long userscount;
    private long inactiveuserscount;

    public Dashboard getDashboard() {
        return dashboard;
    }

    public long getActiveuserscount() {
        return activeuserscount;
    }

    public long getInactiveuserscount() {
        return inactiveuserscount;
    }

    public long getUserscount() {
        return userscount;
    }

    public void setActiveuserscount(long activeuserscount) {
        this.activeuserscount = activeuserscount;
    }

    public void setInactiveuserscount(long inactiveuserscount) {
        this.inactiveuserscount = inactiveuserscount;
    }

    public void setUserscount(long userscount) {
        this.userscount = userscount;
    }

    public List<Author> getUsers() {
        return users;
    }

    public List<AdminPost> getPosts() {
        return posts;
    }

    public void setPosts(List<AdminPost> posts) {
        this.posts = posts;
    }

    public List<ReportResponse> getReports() {
        return reports;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public void setUsers(List<Author> users) {
        this.users = users;
    }

    public void setReports(List<ReportResponse> reports) {
        this.reports = reports;
    }

}
