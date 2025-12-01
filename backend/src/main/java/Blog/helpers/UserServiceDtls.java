package Blog.helpers;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import Blog.model.User;
import Blog.repository.UserRepository;;

@Service
public class UserServiceDtls implements UserDetailsService{
   public final UserRepository repository;
   
    public UserServiceDtls(UserRepository repository) {
        this.repository = repository;
    }
        @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())))
                .build();
    }
}
