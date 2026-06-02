package com.carPooling.backend.security;

import com.carPooling.backend.utils.StringConstant;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(jwt);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            sendError(res, HttpServletResponse.SC_UNAUTHORIZED, StringConstant.ACCESS_TOKEN_EXPIRED,
                    "JWT token has expired. Please refresh your token.");
            return;                          // ← don't continue the filter chain

        } catch (UsernameNotFoundException e) {
            logger.debug(
                    "User not found for email extracted from JWT: {}. Error: {}" + e.getMessage()
            );
            sendError(
                    res,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "",
                    "unauthorized access."
            );
            return;

        } catch (JwtException e) {           // covers tampered / malformed / bad sig
            sendError(res, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALID",
                    "Invalid JWT token.");
            return;
        }

        chain.doFilter(req, res);
    }

    private void sendError(HttpServletResponse res,
                           int status, String error, String message)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.getWriter().write("""
                {"status": false, "error": "%s", "message": "%s", "data": null}
                """.formatted(error, message)
        );
    }


}