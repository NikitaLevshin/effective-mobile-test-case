package effectivemobile.testcase.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Объект пользователя при обновлении данных")
public class EditUserDto {


    @Email(message = "Email должен быть формата login@host.domen")
    @NotEmpty(message = "Поле email не может быть пустым")
    @Size(min = 6, max = 50, message = "Email должен быть длиннее 6 символов")
    private String email;

    @Size(min = 2, max = 15, message = "Имя должно быть не короче 2, и не длиннее 15 символов")
    private String firstName;

    @Size(min = 2, max = 30, message = "Фамилия должна быть не короче 2, и не длиннее 30 символов")
    private String lastName;

}
