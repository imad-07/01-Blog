package Blog.model;

import java.util.List;

public class Dashboard {
    private long totalactiveusers;
    private long totalsuspendedusers;
    private long totalusers;
    private long totalposts;
    private long totalactiveposts;
    private long totalsuspendedposts;
    private long totalcomments;
    private long totalpendingreports;
    private long totalhandledreports;
    private long totalreports;
    private Author star;
    private PostResponse mostlikedpost;
    private List<Author> latestusers;

    public long getTotalcomments() {
        return totalcomments;
    }

    public PostResponse getMostlikedpost() {
        return mostlikedpost;
    }

    public Author getStar() {
        return star;
    }

    public long getTotalactiveposts() {
        return totalactiveposts;
    }

    public long getTotalreports() {
        return totalreports;
    }

    public long getTotalactiveusers() {
        return totalactiveusers;
    }

    public long getTotalsuspendedposts() {
        return totalsuspendedposts;
    }

    public long getTotalsuspendedusers() {
        return totalsuspendedusers;
    }

    public List<Author> getLatestusers() {
        return latestusers;
    }

    public long getTotalhandledreports() {
        return totalhandledreports;
    }

    public long getTotalpendingreports() {
        return totalpendingreports;
    }

    public long getTotalposts() {
        return totalposts;
    }

    public long getTotalusers() {
        return totalusers;
    }

    public void setTotalcomments(long totalcomments) {
        this.totalcomments = totalcomments;
    }

    public void setMostlikedpost(PostResponse mostlikedpost) {
        this.mostlikedpost = mostlikedpost;
    }

    public void setStar(Author star) {
        this.star = star;
    }

    public void setTotalactiveposts(long totalactiveposts) {
        this.totalactiveposts = totalactiveposts;
    }

    public void setTotalreports(long totalreports) {
        this.totalreports = totalreports;
    }

    public void setTotalactiveusers(long totalactiveusers) {
        this.totalactiveusers = totalactiveusers;
    }

    public void setTotalsuspendedposts(long totalsuspendedposts) {
        this.totalsuspendedposts = totalsuspendedposts;
    }

    public void setTotalsuspendedusers(long totalsuspendedusers) {
        this.totalsuspendedusers = totalsuspendedusers;
    }

    public void setTotalhandledreports(long totalhandledreports) {
        this.totalhandledreports = totalhandledreports;
    }

    public void setLatestusers(List<Author> latestusers) {
        this.latestusers = latestusers;
    }

    public void setTotalpendingreports(long totalpendingreports) {
        this.totalpendingreports = totalpendingreports;
    }

    public void setTotalposts(long totalposts) {
        this.totalposts = totalposts;
    }

    public void setTotalusers(long totalusers) {
        this.totalusers = totalusers;
    }
}
