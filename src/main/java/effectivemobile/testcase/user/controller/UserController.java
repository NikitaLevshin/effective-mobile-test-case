package effectivemobile.testcase.user.controller;

import effectivemobile.testcase.user.dto.EditUserDto;
import effectivemobile.testcase.user.dto.UserDto;
import effectivemobile.testcase.user.mapper.UserMapper;
import effectivemobile.testcase.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи", description = "Взаимодействие с пользователями")
@SecurityRequirement(name = "JWT")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение пользователя по id")
    public UserDto getUserById(@PathVariable int id) {
        log.info("Request to get user by id: {}", id);
        return UserMapper.toUserDto(userService.findById(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение списка всех пользователей")
    public List<UserDto> getAllUsers() {
        log.info("Request to get all users");
        return userService.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Редактирование данных пользователя")
    public UserDto updateUser(@RequestBody EditUserDto editUserDto) {
        log.info("Request to update user");
        return UserMapper.toUserDto(userService.update(editUserDto));
    }
}
