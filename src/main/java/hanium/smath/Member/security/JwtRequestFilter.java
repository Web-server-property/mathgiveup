package hanium.smath.Member.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("customMemberDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
            final String authorizationHeader = request.getHeader("Authorization");

            String login_id = null;
            String jwt = null;

            System.out.println("Authorization Header: " + authorizationHeader);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                System.out.println("Extracted JWT: " + jwt);
                try {
                    login_id = jwtUtil.extractLoginId(jwt);
                    System.out.println("Extracted Login ID: " + login_id);

                    // 토큰 만료 시간 출력
                    Date expirationDate = jwtUtil.getExpirationDateFromToken(jwt);
                    System.out.println("Token Expiration Date: " + expirationDate);

                } catch (Exception e) {
                    System.out.println("Error extracting login ID from JWT: " + e.getMessage());
                }
            } else {
                System.out.println("Authorization Header is either null or does not start with 'Bearer '");
            }

            if (login_id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(login_id);
                    System.out.println("Loaded UserDetails for loginId: " + login_id);

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        System.out.println("JWT Token is valid");

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        System.out.println("User authenticated and security context set");
                    } else {
                        System.out.println("JWT Token is invalid");
                    }
                } catch (Exception e) {
                    System.out.println("Error loading UserDetails or validating token: " + e.getMessage());
                }
            } else {
                System.out.println("Login ID is null or Authentication is already set");
            }

        filterChain.doFilter(request, response);
        System.out.println("Filter chain continued");

        // 추가 로그
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("Security Context contains: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        } else {
            System.out.println("Security Context is empty after filter chain");
        }
    }
}

