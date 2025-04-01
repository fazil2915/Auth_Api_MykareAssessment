package Config;

import io.jsonwebtoken.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("CSRF disabled check - request: " + request.getRequestURI());
        String requestURI = request.getRequestURI();
        System.out.println("Incoming request: " + requestURI);


        if (requestURI.contains("/api/auth/register") || requestURI.contains("/api/auth/login")) {
            System.out.println("Skipping JWT filter for: " + requestURI);
            chain.doFilter(request, response);
            return;
        }


        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }


        // Extract JWT Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Missing or invalid Authorization header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("Extracted JWT Token: " + token);

        try {
            String email = jwtUtil.extractEmail(token);
            System.out.println("Extracted email: " + email);
            if (jwtUtil.validateToken(token, email)) {
                System.out.println("JWT is valid for user: " + email);
                chain.doFilter(request, response);
            } else {
                System.out.println("Invalid or expired JWT Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            }
        } catch (JwtException e) {
            System.out.println("JWT Exception: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }

    }
}
