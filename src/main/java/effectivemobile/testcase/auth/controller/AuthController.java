package effectivemobile.testcase.auth.controller;

import effectivemobile.testcase.auth.config.SecurityConfigurator;
import effectivemobile.testcase.auth.model.JwtRequest;
import effectivemobile.testcase.auth.model.JwtResponse;
import effectivemobile.testcase.auth.model.RefreshRequest;
import effectivemobile.testcase.auth.service.AuthServiceImpl;
import effectivemobile.testcase.user.dto.NewUserDto;
import effectivemobile.testcase.user.dto.UserDto;
import effectivemobile.testcase.user.mapper.UserMapper;
import effectivemobile.testcase.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Регистрация и авторизация", description = "Регистрация, авторизация и аутентификация по токену")
public class AuthController {

    private final UserService userService;
    private final AuthServiceImpl authServiceImpl;
    private final SecurityConfigurator securityConfigurator;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация пользователя")
    public UserDto registerUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Request for register new user");
        newUserDto.setPassword(securityConfigurator.passwordEncoder().encode(newUserDto.getPassword()));
        return UserMapper.toUserDto(userService.save(newUserDto));
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация пользователя")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest loginRequest) {
        log.info("Request for login");
        JwtResponse jwtResponse = authServiceImpl.login(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/token")
    @Operation(summary = "Получение нового access токена")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshRequest refreshRequest) {
        log.info("Request to get new access token");
        JwtResponse jwtResponse = authServiceImpl.getAccessToken(refreshRequest.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Получение новой пары access/refresh токенов")
    @SecurityRequirement(name = "JWT")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        log.info("Request to refresh token");
        JwtResponse jwtResponse = authServiceImpl.refreshToken(refreshRequest.getRefreshToken());
        return ResponseEntity.ok(jwtResponse);
    }
}
