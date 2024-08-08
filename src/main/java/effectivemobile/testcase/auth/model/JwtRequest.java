package effectivemobile.testcase.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект пользователя, отправляемый прри авторизации")
public class JwtRequest {

    @Email(message = "Email должен быть формата login@host.domen")
    @NotEmpty(message = "Поле email не может быть пустым")
    @Size(min = 6, max = 50, message = "Email должен быть длиннее 6 символов")
    private String email;

    @NotEmpty(message = "Поле password не может быть пустым")
    @Size(min = 6, max = 50, message = "Пароль должен быть длиннее 6 символов")
    private String password;

}
