package effectivemobile.testcase.auth.config;

import effectivemobile.testcase.exceptions.AuthException;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtCore jwtCore;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String email = null;
        UsernamePasswordAuthenticationToken authentication = null;
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                jwt = header.substring(7);
            }
            if (jwt != null && jwtCore.validateAccessToken(jwt)) {
                Claims claims = jwtCore.getAccessClaims(jwt);
                email = claims.getSubject();
            }
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userService.findByEmail(email);
                authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            throw new AuthException("Ошибка аутентификации");
        }
        filterChain.doFilter(request, response);
    }
}
