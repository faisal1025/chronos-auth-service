package com.chronos.AuthService.filter;

import com.chronos.AuthService.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;

    private UserDetailsService userDetailsService;

    public AuthorizationFilter(@Autowired JwtUtil jwtUtil, @Autowired UserDetailsService userDetailService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        String username = null;
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception ex) {
                // invalid token - proceed without authentication
            }
        }



        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                Claims claims = jwtUtil.extractAllClaims(token);

                Map<String, Object> details = new HashMap<>();

                details.put("userId", claims.get("userId", Long.class));
                details.put("email", claims.get("email", String.class));

                authToken.setDetails(details);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
