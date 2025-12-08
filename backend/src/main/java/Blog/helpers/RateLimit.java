package Blog.helpers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import Blog.model.User;
import Blog.repository.UserRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimit extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    public final UserRepository repository;
    private final ConcurrentHashMap<Long, Bucket> Ref = new ConcurrentHashMap<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth/**",
            "/guard",
            "/avatars/**",
            "/files/posts/**",
            "/test-image/**");

    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    public RateLimit(JwtUtil jwtUtil, UserRepository repo) {
        this.jwtUtil = jwtUtil;
        this.repository = repo;
    }

    public Bucket getOrCreateBucketForUser(long userid) {
        Bucket b = Ref.get(userid);
        if (b == null) {
            Bandwidth bandwidth = Bandwidth.builder()
                    .capacity(10)
                    .refillIntervally(100, Duration.ofSeconds(1))
                    .build();
            b = Bucket.builder()
                    .addLimit(bandwidth)
                    .build();

            Ref.put(userid, b);
        }
        return b;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isExcluded(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }
        User user = repository.findByUsername(username).orElse(null);
        if (user != null) {

            Bucket bucket = getOrCreateBucketForUser(user.getId());
            if (!bucket.tryConsume(1)) {
                System.out.println("user banned for too many requests");
                response.setStatus(429);
                response.getWriter().write("Too Many Requests Try After");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
