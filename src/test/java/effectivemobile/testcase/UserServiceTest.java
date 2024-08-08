package effectivemobile.testcase;

import effectivemobile.testcase.exceptions.NotFoundException;
import effectivemobile.testcase.user.dto.EditUserDto;
import effectivemobile.testcase.user.dto.NewUserDto;
import effectivemobile.testcase.user.model.Role;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.repository.UserRepository;
import effectivemobile.testcase.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;
    private final User user = new User(1L, "test@test.ru",
            "password", "Test", "User", null, Role.ROLE_USER);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetails() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), userDetails.getUsername());
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.loadUserByUsername(user.getEmail()));
    }

    @Test
    public void saveShouldReturnSavedUser() {
        NewUserDto newUserDto = new NewUserDto(user.getEmail(),
                user.getPassword(), user.getFirstName(), user.getLastName());

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(newUserDto);

        assertEquals(user.getId(), savedUser.getId());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    public void updateRefreshTokenShouldUpdateRefreshToken() {
        String newRefreshToken = "newRefreshToken";

        userService.updateRefreshToken(user, newRefreshToken);

        assertEquals(newRefreshToken, user.getRefreshToken());
    }

    @Test
    public void findByEmailShouldReturnUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User foundUser = userService.findByEmail(user.getEmail());

        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    public void findByEmailShouldThrowNotFoundException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmail(user.getEmail()));

    }

    @Test
    public void findByIdShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User foundUser = userService.findById(user.getId());

        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    public void findByIdShouldThrowNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(user.getId()));

    }

    @Test
    public void findAllShouldReturnAllUsers() {
        User user2 = user;
        user2.setId(2L);
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<User> users = userService.findAll();

        assertEquals(2, users.size());
    }

    @Test
    public void updateShouldUpdateUser() {
        EditUserDto editUserDto = new EditUserDto("updatemail@test.ru",
                "NewFirstName", "NewLastName");

        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userService.getAuthenticatedUser()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User updatedUser = userService.update(editUserDto);

        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getFirstName(), updatedUser.getFirstName());
        assertEquals(user.getLastName(), updatedUser.getLastName());
    }
}

