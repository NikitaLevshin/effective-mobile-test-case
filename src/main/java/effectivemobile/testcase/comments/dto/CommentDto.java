package effectivemobile.testcase.comments.dto;

import effectivemobile.testcase.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(description = "Объект комментария, возвращаемый при запросах")
public class CommentDto {

    private long id;
    private String comment;
    private long taskId;
    private UserDto user;
}
