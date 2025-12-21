package Blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.helpers.validators;
import Blog.model.Author;
import Blog.model.Comment;
import Blog.model.CommentResponse;
import Blog.model.NotificationType;
import Blog.model.Post;
import Blog.model.User;
import Blog.repository.CommentRepository;
import Blog.repository.PostRepository;

@Service
public class CommentService {
    private CommentRepository commentrepository;
    private PostRepository postrepository;
    private UserService userService;
    private NotificationService notifservice;

    public CommentService(CommentRepository commentrepository, UserService userService, PostRepository postrepository,
            NotificationService notifservice) {
        this.commentrepository = commentrepository;
        this.userService = userService;
        this.postrepository = postrepository;
        this.notifservice = notifservice;
    }

    public Integer CountPostLike(long id) {
        return commentrepository.countByPostId(id);
    }

    public CommentResponse AddComment(UserDetails usr, long postid, String cmnt) {
        User user = userService.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            throw new Blog.exception.NotFoundException("User not found");
        }
        Post post = postrepository.findById(postid).orElse(null);
        if (post == null || !post.isStatus()) {
            throw new Blog.exception.NotFoundException("Post not found");
        }
        if (!validators.ValidateContent(cmnt)) {
            throw new Blog.exception.BadRequestException("Invalid comment length");
        }
        Comment cmt = new Comment(user, post, cmnt);
        commentrepository.save(cmt);
        User reciever = userService.getUserByUsername(post.getAuthor().getUsername()).orElse(null);
        if (reciever == null) {
            throw new Blog.exception.NotFoundException("Post author not found");
        }
        notifservice.save(user, reciever, NotificationType.COMMENT);

        CommentResponse r = new CommentResponse();
        r.setId(cmt.getId());
        r.setContent(cmt.getContent());
        Author a = new Author(user.getId(), user.getAvatar(), user.getUsername(), user.isStatus(), user.getRole());
        r.setAuthor(a);
        return r;
    }

    public List<CommentResponse> GetComments(long id, long start) {
        if (start == 1) {
            return new ArrayList<>();
        }
        if (start == 0) {
            Comment c = commentrepository.findTopByOrderByIdDesc();
            if (c == null) {
                return new ArrayList<>();
            }
            start = c.getId() + 1;
        }
        List<Comment> cmts = commentrepository.findTop10ByPostIdAndIdLessThanOrderByIdDesc(id, start);
        List<CommentResponse> rslt = new ArrayList<>();
        for (Comment c : cmts) {
            CommentResponse r = new CommentResponse();
            r.setId(c.getId());
            r.setContent(c.getContent());
            User u = c.getUser();
            if (u == null) {
                return new ArrayList<>();
            }
            Author a = new Author(u.getId(), u.getAvatar(), u.getUsername(), u.isStatus(), u.getRole());
            r.setAuthor(a);
            // r.setTimestamp(c.getTimestamp().toString());
            rslt.add(r);
        }
        return rslt;
    }

    public void DeleteComment(UserDetails user, long id) {
        Comment c = commentrepository.findById(id)
                .orElseThrow(() -> new Blog.exception.NotFoundException("Comment not found"));

        User u = userService.getUserByUsername(user.getUsername()).orElse(null);
        if (u == null) {
            throw new Blog.exception.NotFoundException("User not found");
        }
        if (c.getUser().getId() != u.getId()) {
            throw new Blog.exception.ForbiddenException("You can only delete your own comments");
        }
        commentrepository.deleteByUserIdAndId(u.getId(), c.getId());
    }

    public Integer Getcommentcount(long id) {
        return commentrepository.countByPostId(id);
    }

    public long countComments() {
        return commentrepository.count();
    }
}
