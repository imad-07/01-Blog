package Blog.helpers;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import Blog.model.User;
import Blog.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BanFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    public final UserRepository repository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth/**",
            "/avatars/**",
            "/files/posts/**",
            "/test-image/**");

    public BanFilter(JwtUtil jwtUtil, UserRepository repo) {
        this.jwtUtil = jwtUtil;
        this.repository = repo;
    }

    private boolean isRestrictedMethod(String method) {
        return method.equals("POST") || method.equals("PATCH") || method.equals("DELETE");
    }

    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        String method = request.getMethod();

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        if (username != null) {
            User user = repository.findByUsername(username).orElse(null);

            if (user != null && !user.isStatus() && isRestrictedMethod(method)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("You are banned");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
