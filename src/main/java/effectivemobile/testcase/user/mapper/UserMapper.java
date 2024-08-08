package effectivemobile.testcase.user.mapper;

import effectivemobile.testcase.user.dto.NewUserDto;
import effectivemobile.testcase.user.dto.UserDto;
import effectivemobile.testcase.user.model.Role;
import effectivemobile.testcase.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static User fromNewUserDto(NewUserDto newUserDto) {
        User user = new User();
        user.setEmail(newUserDto.getEmail());
        user.setPassword(newUserDto.getPassword());
        user.setFirstName(newUserDto.getFirstName() != null ? newUserDto.getFirstName() : null);
        user.setLastName(newUserDto.getLastName() != null ? newUserDto.getLastName() : null);
        user.setRole(Role.ROLE_USER);
        return user;
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();


    }
}
