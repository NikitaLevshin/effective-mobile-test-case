package effectivemobile.testcase.task.mapper;

import effectivemobile.testcase.comments.mapper.CommentMapper;
import effectivemobile.testcase.task.dto.NewTaskDto;
import effectivemobile.testcase.task.dto.TaskDto;
import effectivemobile.testcase.task.model.Task;
import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;
import effectivemobile.testcase.user.mapper.UserMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskMapper {
    public static Task fromNewTask(NewTaskDto newTaskDto) {
        Task task = new Task();
        task.setTitle(newTaskDto.getTitle());
        task.setDescription(newTaskDto.getDescription() != null ? newTaskDto.getDescription() : null);
        task.setStatus(newTaskDto.getStatus() != null ? newTaskDto.getStatus() : TaskStatus.PENDING);
        task.setPriority(newTaskDto.getPriority() != null ? newTaskDto.getPriority() : TaskPriority.MEDIUM);
        return task;
    }

    public static TaskDto toTaskDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .creator(UserMapper.toUserDto(task.getCreator()))
                .performer(task.getPerformer() != null ? UserMapper.toUserDto(task.getPerformer()) : null)
                .comments(CommentMapper.toCommentDtoList(task.getComments()))
                .build();
    }
}
