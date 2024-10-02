package com.example.ServerSpring.secutiry;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // Список URL, для которых не нужно проверять JWT
    private static final List<String> EXCLUDE_URLS = new ArrayList<>();

    static {
        EXCLUDE_URLS.add("/auth/login");
        EXCLUDE_URLS.add("/auth/register");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        System.out.println("Processing request: " + requestURI);
        System.out.println("Received Authorization Header: " + authorizationHeader);

        if (EXCLUDE_URLS.contains(requestURI)) {
            System.out.println("Request URI is excluded from JWT validation: " + requestURI);
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JWT Token: " + jwt);
            try {
                username = jwtUtil.extractUsername(jwt, jwtUtil.getSecretKey());
                System.out.println("Extracted username from JWT: " + username);
            } catch (Exception e) {
                System.out.println("JWT Token extraction failed: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, jwtUtil.getSecretKey())) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                System.out.println("JWT Token validated successfully for user: " + username);
            } else {
                System.out.println("JWT Token validation failed for user: " + username);
            }
        } else {
            System.out.println("Username is null or already authenticated: " + username);
        }

        chain.doFilter(request, response);
    }
}