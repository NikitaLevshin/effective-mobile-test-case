package effectivemobile.testcase;

import effectivemobile.testcase.auth.config.JwtCore;
import effectivemobile.testcase.auth.model.JwtRequest;
import effectivemobile.testcase.auth.model.JwtResponse;
import effectivemobile.testcase.auth.service.AuthServiceImpl;
import effectivemobile.testcase.exceptions.AuthException;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.service.UserService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtCore jwtCore;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;
    private final String EMAIL = "test@test.ru";
    private final User user = new User();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user.setEmail(EMAIL);
    }

    @Test
    public void loginShouldReturnTokensWithValidCredentials() {
        String password = "password";
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setEmail(EMAIL);
        jwtRequest.setPassword(password);

        user.setRefreshToken("oldRefreshToken");

        Authentication authentication = new UsernamePasswordAuthenticationToken(EMAIL, password);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(userService.findByEmail(EMAIL)).thenReturn(user);
        when(jwtCore.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtCore.generateRefreshToken(user)).thenReturn("refreshToken");

        JwtResponse jwtResponse = authService.login(jwtRequest);

        assertNotNull(jwtResponse);
        assertEquals("accessToken", jwtResponse.getAccessToken());
        assertEquals("refreshToken", jwtResponse.getRefreshToken());

    }

    @Test
    public void loginShouldThrowAuthExceptionWhenCredentialsAreInvalid() {
        String password = "wrongPassword";
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setEmail(EMAIL);
        jwtRequest.setPassword(password);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        AuthException exception = assertThrows(AuthException.class, () -> authService.login(jwtRequest));

        assertEquals("Неверный логин или пароль", exception.getMessage());
    }

    @Test
    public void getAccessTokenShouldReturnNewAccessTokenWhenRefreshTokenIsValid() {
        String refreshToken = "validRefreshToken";

        user.setRefreshToken(refreshToken);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(user.getEmail());

        when(jwtCore.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtCore.getRefreshClaims(refreshToken)).thenReturn(claims);
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(jwtCore.generateAccessToken(user)).thenReturn("newAccessToken");

        JwtResponse jwtResponse = authService.getAccessToken(refreshToken);

        assertNotNull(jwtResponse);
        assertEquals("newAccessToken", jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());
    }

    @Test
    public void getAccessTokenShouldReturnNullWhenRefreshTokenIsInvalid() {
        String refreshToken = "invalidRefreshToken";

        when(jwtCore.validateRefreshToken(refreshToken)).thenReturn(false);

        JwtResponse jwtResponse = authService.getAccessToken(refreshToken);

        assertNull(jwtResponse.getAccessToken());
    }

    @Test
    public void refreshTokenShouldReturnNewTokensWhenRefreshTokenIsValid() {
        String refreshToken = "validRefreshToken";

        user.setRefreshToken(refreshToken);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(user.getEmail());

        when(jwtCore.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtCore.getRefreshClaims(refreshToken)).thenReturn(claims);
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(jwtCore.generateAccessToken(user)).thenReturn("newAccessToken");
        when(jwtCore.generateRefreshToken(user)).thenReturn("newRefreshToken");

        JwtResponse jwtResponse = authService.refreshToken(refreshToken);

        assertNotNull(jwtResponse);
        assertEquals("newAccessToken", jwtResponse.getAccessToken());
        assertEquals("newRefreshToken", jwtResponse.getRefreshToken());
    }

    @Test
    public void refreshTokenShouldThrowAuthExceptionWhenRefreshTokenIsInvalid() {
        String refreshToken = "invalidRefreshToken";

        when(jwtCore.validateRefreshToken(refreshToken)).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> authService.refreshToken(refreshToken));

        assertEquals("Invalid JWT token", exception.getMessage());
    }

    @Test
    public void getAuthenticatedUserShouldReturnCurrentAuthenticatedUser() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User authenticatedUser = authService.getAuthenticatedUser();

        assertEquals(user, authenticatedUser);
    }
}
