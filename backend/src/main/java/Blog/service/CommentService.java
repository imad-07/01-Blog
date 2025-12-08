package Blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.helpers.validators;
import Blog.model.Author;
import Blog.model.Comment;
import Blog.model.CommentResponse;
import Blog.model.Errors;
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

    public Errors.Comment_Error AddComment(UserDetails usr, long postid, String cmnt) {
        User user = userService.getUserByUsername(usr.getUsername()).orElse(null);
        if (user == null) {
            return Errors.Comment_Error.InvalidUser;
        }
        Post post = postrepository.findById(postid).orElse(null);
        if (post == null) {
            return Errors.Comment_Error.InvalidPost;
        }
        if (!validators.ValidateContent(cmnt)) {
            return Errors.Comment_Error.InvalidLength;
        }
        Comment cmt = new Comment(user, post, cmnt);
        commentrepository.save(cmt);
        User reciever = userService.getUserByUsername(post.getAuthor().getUsername()).orElse(null);
        if (reciever == null) {
            return Errors.Comment_Error.InvalidUser;
        }
        notifservice.save(user, reciever, NotificationType.COMMENT);
        return Errors.Comment_Error.Success;
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
            Author a = new Author(u.getId(), u.getAvatar(), u.getUsername(), u.isStatus());
            r.setAuthor(a);
            rslt.add(r);
        }
        return rslt;
    }

    public String DeleteComment(UserDetails user, long id) {
        Comment c = new Comment();
        try {
            c = commentrepository.getReferenceById(id);
        } catch (Exception e) {
            return "invalid comment";
        }
        User u = userService.getUserByUsername(user.getUsername()).orElse(null);
        if (u == null) {
            return "invalid user";
        }
        if (c.getUser().getId() != u.getId()) {
            return "No";
        }
        commentrepository.deleteByUserIdAndId(u.getId(), c.getId());
        return "Done";
    }

    public Integer Getcommentcount(long id) {
        return commentrepository.countByPostId(id);
    }

    public long countComments() {
        return commentrepository.count();
    }
}
