package com.striveconnect.config;

import com.striveconnect.service.JwtService;
import com.striveconnect.util.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ✅ Handles JWT token validation and tenant context setup.
 * Skips authentication for allowed public endpoints (GET only).
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String httpMethod = request.getMethod();
        String authorizationHeader = request.getHeader("Authorization");
        String tenantId = request.getParameter("tenantId"); // ✅ tenantId from query params

        System.out.println("JwtRequestFilter running for path: " + requestPath);

        try {
            // 🔓 Skip JWT validation for public endpoints
            if (isPublicEndpoint(request)) {
                System.out.println("🔓 Public endpoint matched (GET only): " + requestPath);
                if (tenantId != null) {
                    TenantContext.setCurrentTenant(tenantId);
                }
                chain.doFilter(request, response);
                return;
            }

            // Extract JWT if present
            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtService.extractUsername(jwt);
                if (tenantId == null) {
                    tenantId = jwtService.extractTenantId(jwt);
                }
            }

            // Authenticate the user if JWT is valid
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Set tenant context (important for multi-tenant logic)
                TenantContext.setCurrentTenant(tenantId);

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            chain.doFilter(request, response);

        } finally {
            // ✅ Always clear tenant context to avoid thread leakage
            TenantContext.clear();
        }
    }

    // ------------------------------------------------------------------------
    // 🔍 Public Endpoint Rules (GET only for some APIs)
    // ------------------------------------------------------------------------

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // ✅ Allow GET requests to these endpoints (public content)
        if ("GET".equalsIgnoreCase(method)) {
            return path.startsWith("/api/courses")
                    || path.startsWith("/api/centers")
                    || path.startsWith("/api/batches")
                    || path.startsWith("/api/content")
                    || path.startsWith("/api/files/download");
        }

        // ✅ Always public (for any method)
        return path.startsWith("/api/auth")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/actuator");
    }
}
