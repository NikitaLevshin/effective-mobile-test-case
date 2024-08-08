package effectivemobile.testcase.task.dto;

import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;
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
@Schema(description = "Объект задачи при её обновлении")
public class EditTaskDto {

    @NotEmpty(message = "Название не может быть пустым")
    private String title;

    @Size(min = 10, max = 500, message = "Описание должно быть не короче 10 и не больше 500 символов")
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private long performerId;

}
