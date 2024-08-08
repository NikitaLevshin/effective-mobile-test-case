package effectivemobile.testcase.comments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Объект комментария при его создании")
public class NewCommentDto {

    @NotEmpty(message = "Комментарий не может быть пустым")
    @Size(min = 5, max = 1000, message = "Комментарий не должен быть короче 5 символов, и длинее 1000")
    private String comment;
}
