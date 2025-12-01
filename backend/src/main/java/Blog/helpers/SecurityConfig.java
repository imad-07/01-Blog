package Blog.helpers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    //private final BanFilter banFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
       // this.banFilter = banFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println(http.toString());
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/avatars/**").permitAll()
                        .requestMatchers("/files/posts/**").permitAll()
                        .requestMatchers("/test-image/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) -> {
                            System.out.println("========== AUTHENTICATION FAILED ==========");
                            System.out.println("Request: " + req.getMethod() + " " + req.getRequestURI());
                            System.out.println("Remote Address: " + req.getRemoteAddr());
                            System.out.println("Error: " + authEx.getMessage());
                            System.out.println("Exception Type: " + authEx.getClass().getName());
                            System.out.println("Authorization Header: " + req.getHeader("Authorization"));
                            System.out.println("===========================================");
                            System.out.println(req.getRequestURL());
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("false");
                        })
                        .accessDeniedHandler((req, res, accessEx) -> {
                            System.out.println("========== ACCESS DENIED ==========");
                            System.out.println("Request: " + req.getMethod() + " " + req.getRequestURI());
                            System.out.println(
                                    "User: " + (req.getUserPrincipal() != null ? req.getUserPrincipal().getName()
                                            : "anonymous"));
                            System.out.println("Reason: " + accessEx.getMessage());
                            System.out.println("====================================");

                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("false");
                        }));

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
       // http.addFilterAfter(banFilter, JwtAuthFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
