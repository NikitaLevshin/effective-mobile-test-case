package effectivemobile.testcase.task.service;

import effectivemobile.testcase.auth.service.AuthService;
import effectivemobile.testcase.exceptions.ForbiddenException;
import effectivemobile.testcase.exceptions.NotFoundException;
import effectivemobile.testcase.task.dto.EditTaskDto;
import effectivemobile.testcase.task.dto.NewTaskDto;
import effectivemobile.testcase.task.mapper.TaskMapper;
import effectivemobile.testcase.task.model.Task;
import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;
import effectivemobile.testcase.task.repository.TaskRepository;
import effectivemobile.testcase.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final AuthService authService;
    private final UserService userService;

    @Override
    @Transactional
    public Task createTask(NewTaskDto newTaskDto) {
        log.info("Creating new task: {}", newTaskDto);
        Task task = TaskMapper.fromNewTask(newTaskDto);
        task.setCreator(authService.getAuthenticatedUser());
        if (newTaskDto.getPerformerId() != 0) {
            task.setPerformer(userService.findById(newTaskDto.getPerformerId()));
        }
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(long id) {
        log.info("Retrieving task by id: {}", id);
        return taskRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Задание с этим id не найдено"));

    }

    @Override
    public List<Task> getAllTasks(String title, TaskStatus taskStatus, TaskPriority taskPriority, Long creator, Long performer,
                                  Integer size) {
        log.info("Retrieving all tasks");
        Pageable page = PageRequest.of(0, size);
        return taskRepository.findAll(title, taskStatus, taskPriority, creator, performer, page);
    }

    @Override
    @Transactional
    public Task updateTaskStatus(long id, TaskStatus taskStatus) {
        log.info("Updating task {} status: {}", id, taskStatus);
        Task task = getTaskById(id);
        long performerId = 0;
        if (task.getPerformer() != null) {
            performerId = task.getPerformer().getId();
        }
        if (authService.getAuthenticatedUser().getId() != task.getCreator().getId()
                && authService.getAuthenticatedUser().getId() != performerId) {
            throw new ForbiddenException("У вас недостаточно прав для изменения статуса задания");
        } else {
            task.setStatus(taskStatus);
        }
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTask(long id) {
        log.info("Deleting task: {}", id);
        Task task = getTaskById(id);
        if (authService.getAuthenticatedUser().getId() != task.getCreator().getId()) {
            throw new ForbiddenException("У вас недостаточно прав для удаления задачи");
        }
        taskRepository.delete(getTaskById(id));
    }

    @Override
    @Transactional
    public Task editTask(long id, EditTaskDto editTaskDto) {
        log.info("Updating task {} with editTaskDto: {}", id, editTaskDto);
        Task task = getTaskById(id);
        if (authService.getAuthenticatedUser().getId() != task.getCreator().getId()) {
            throw new ForbiddenException("У вас недостаточно прав для редактирования задачи");
        }
        if (editTaskDto.getTitle() != null) {
            task.setTitle(editTaskDto.getTitle());
        }
        if (editTaskDto.getDescription() != null) {
            task.setDescription(editTaskDto.getDescription());
        }
        if (editTaskDto.getStatus() != null) {
            task.setStatus(editTaskDto.getStatus());
        }
        if (editTaskDto.getPriority() != null) {
            task.setPriority(editTaskDto.getPriority());
        }
        if (editTaskDto.getPerformerId() != 0) {
            task.setPerformer(userService.findById(editTaskDto.getPerformerId()));
        }
        return taskRepository.save(task);
    }
}
