package effectivemobile.testcase.user.service;

import effectivemobile.testcase.exceptions.NotFoundException;
import effectivemobile.testcase.user.dto.EditUserDto;
import effectivemobile.testcase.user.dto.NewUserDto;
import effectivemobile.testcase.user.mapper.UserMapper;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }

    @Override
    @Transactional
    public User save(NewUserDto newUserDto) {
        log.info("Saving new user: {}", newUserDto);
        User user = UserMapper.fromNewUserDto(newUserDto);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateRefreshToken(User user, String refreshToken) {
        log.info("Updating refresh token for user: {}", user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким email не существует"));
    }

    @Override
    public User findById(long id) {
        log.info("Finding user by id: {}", id);
        return userRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует"));
    }

    @Override
    public List<User> findAll() {
        log.info("Finding all users");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User update(EditUserDto editUserDto) {
        log.info("Updating user");
        User user = findById(getAuthenticatedUser().getId());
        if (editUserDto.getEmail() != null) {
            user.setEmail(editUserDto.getEmail());
        }
        if (editUserDto.getFirstName() != null) {
            user.setFirstName(editUserDto.getFirstName());
        }
        if (editUserDto.getLastName() != null) {
            user.setLastName(editUserDto.getLastName());
        }
        return userRepository.save(user);
    }

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
