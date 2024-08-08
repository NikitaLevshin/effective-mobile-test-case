package effectivemobile.testcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import effectivemobile.testcase.auth.config.SecurityConfigurator;
import effectivemobile.testcase.auth.controller.AuthController;
import effectivemobile.testcase.auth.model.JwtRequest;
import effectivemobile.testcase.auth.model.JwtResponse;
import effectivemobile.testcase.auth.model.RefreshRequest;
import effectivemobile.testcase.auth.service.AuthServiceImpl;
import effectivemobile.testcase.user.dto.NewUserDto;
import effectivemobile.testcase.user.model.Role;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthServiceImpl authServiceImpl;

    @Mock
    private SecurityConfigurator securityConfigurator;

    @InjectMocks
    private AuthController authController;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void registerUserShouldReturnCreatedUser() throws Exception {
        NewUserDto newUserDto =
                new NewUserDto("test@test.ru", "qwerty123", "Test", "User");
        User savedUser = new User(1L, "test@test.ru", "encodedPassword",
                "Test", "User", null, Role.ROLE_USER);

        when(userService.save(any(NewUserDto.class))).thenReturn(savedUser);
        when(securityConfigurator.passwordEncoder()).thenReturn(new BCryptPasswordEncoder(8));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(savedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(savedUser.getLastName()));
    }

    @Test
    public void loginShouldReturnJwtResponse() throws Exception {
        JwtRequest loginRequest = new JwtRequest("test@test.ru", "qwerty123");
        JwtResponse jwtResponse = new JwtResponse("accessToken", "refreshToken");

        when(authServiceImpl.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));

    }

    @Test
    public void getNewAccessTokenShouldReturnNewAccessToken() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("refreshToken");
        JwtResponse jwtResponse = new JwtResponse("newAccessToken", "refreshToken");

        when(authServiceImpl.getAccessToken("refreshToken")).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    public void refreshTokenShouldReturnNewBothTokens() throws Exception {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setRefreshToken("refreshToken");
        JwtResponse jwtResponse = new JwtResponse("newAccessToken", "newRefreshToken");

        when(authServiceImpl.refreshToken("refreshToken")).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));

        verify(authServiceImpl).refreshToken("refreshToken");
    }
}

