package org.anta.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath() == null ? "" : request.getServletPath();
        String uri = request.getRequestURI() == null ? "" : request.getRequestURI();
        log.debug("shouldNotFilter? servletPath='{}' uri='{}'", servletPath, uri);
        return servletPath.startsWith("/api/auth/") || uri.startsWith("/api/auth/");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        log.debug("JwtFilter called for path={} method={}", path, request.getMethod());

        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization header: {}", authHeader);

        String token = null;
        String name = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                name = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                log.warn("Failed to extract name from token: {}", e.getMessage());
                // nếu là endpoint public, không trả lỗi cứng; cho tiếp filter chain
                if (path.startsWith("/api/auth/")) {
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        if (name != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(name);
            } catch (Exception e) {
                log.warn("UserDetails not found for name {}: {}", name, e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (!jwtUtil.isTokenValid(token, userDetails)) {
                log.warn("Token is not valid for user {}", name);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            List<String> rolesFromToken = jwtUtil.extractRoles(token);

            List<SimpleGrantedAuthority> authorities = rolesFromToken.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if (authorities.isEmpty()) {
                authorities = userDetails.getAuthorities().stream()
                        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                        .collect(Collectors.toList());
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Set authentication for user {} with authorities {}", name, authorities);
        }

        filterChain.doFilter(request, response);
    }
}