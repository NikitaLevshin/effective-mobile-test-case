package effectivemobile.testcase.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Сущность пользователя, хранящаяся в БД")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    @Email(message = "Email должен быть формата login@host.domen")
    @NotEmpty(message = "Поле email не может быть пустым")
    @Size(min = 6, max = 50, message = "Email должен быть длиннее 6 символов")
    private String email;

    @NotEmpty(message = "Поле password не может быть пустым")
    @Size(min = 6, message = "Пароль должен быть длиннее 6 символов")
    private String password;

    @Column(name = "first_name")
    @Size(min = 2, max = 15, message = "Имя должно быть не короче 2, и не длиннее 15 символов")
    private String firstName;

    @Column(name = "last_name")
    @Size(min = 2, max = 30, message = "Фамилия должна быть не короче 2, и не длиннее 30 символов")
    private String lastName;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
