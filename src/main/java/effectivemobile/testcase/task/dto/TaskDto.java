package effectivemobile.testcase.task.dto;

import effectivemobile.testcase.comments.dto.CommentDto;
import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;
import effectivemobile.testcase.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Schema(description = "Объект задачи возвращающися при запросах")
public class TaskDto {

    private long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UserDto creator;
    private UserDto performer;
    private List<CommentDto> comments;
}
