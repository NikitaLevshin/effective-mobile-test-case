package effectivemobile.testcase.auth.service;

import effectivemobile.testcase.auth.config.JwtCore;
import effectivemobile.testcase.auth.model.JwtRequest;
import effectivemobile.testcase.auth.model.JwtResponse;
import effectivemobile.testcase.exceptions.AuthException;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtCore jwtCore;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public JwtResponse login(@NonNull JwtRequest jwtRequest) {
        log.info("User logging");
        Authentication auth;
        try {
            auth = new UsernamePasswordAuthenticationToken(jwtRequest.getEmail(), jwtRequest.getPassword());
            authenticationManager.authenticate(auth);
        } catch (BadCredentialsException e) {
            throw new AuthException("Неверный логин или пароль");
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = userService.findByEmail(jwtRequest.getEmail());
        String accessToken = jwtCore.generateAccessToken(user);
        String refreshToken = jwtCore.generateRefreshToken(user);
        userService.updateRefreshToken(user, refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        log.info("Get access token");
        if (jwtCore.validateRefreshToken(refreshToken)) {
            Claims claims = jwtCore.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            User user = userService.findByEmail(email);
            if (user.getRefreshToken().equals(refreshToken)) {
                String accessToken = jwtCore.generateAccessToken(user);
                return new JwtResponse(accessToken, refreshToken);
            }
        }
        return new JwtResponse(null, null);
    }

    @Override
    @Transactional
    public JwtResponse refreshToken(@NonNull String refreshToken) {
        log.info("Get refresh token");
        if (jwtCore.validateRefreshToken(refreshToken)) {
            Claims claims = jwtCore.getRefreshClaims(refreshToken);
            String email = claims.getSubject();
            User user = userService.findByEmail(email);
            if (user.getRefreshToken().equals(refreshToken)) {
                String accessToken = jwtCore.generateAccessToken(user);
                String newRefreshToken = jwtCore.generateRefreshToken(user);
                userService.updateRefreshToken(user, newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Invalid JWT token");
    }

    @Override
    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
