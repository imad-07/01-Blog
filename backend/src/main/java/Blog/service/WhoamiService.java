package Blog.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import Blog.model.Author;
import Blog.model.User;

@Service
public class WhoamiService {
    private final UserService us;

    public WhoamiService(UserService us) {
        this.us = us;
    }

    public Author Whoami(UserDetails user) {
        User u = us.getUserByUsername(user.getUsername()).orElse(null);
        Author a = null;
        if (u != null) {
            a = new Author(u.getId(),u.getAvatar(), u.getUsername(), u.isStatus());
        }
        return a;
    }
        public Author Whoami(User user) {
        Author a = null;
        if (user != null) {
            a = new Author(user.getId(),user.getAvatar(), user.getUsername(), user.isStatus());
        }
        return a;
    }
}
