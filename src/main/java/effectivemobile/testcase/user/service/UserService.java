package effectivemobile.testcase.user.service;

import effectivemobile.testcase.user.dto.EditUserDto;
import effectivemobile.testcase.user.dto.NewUserDto;
import effectivemobile.testcase.user.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User save(NewUserDto newUserDto);

    User findByEmail(String email);

    void updateRefreshToken(User user, String refreshToken);

    User findById(long id);

    List<User> findAll();

    User update(EditUserDto editUserDto);
}
