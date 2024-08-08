package effectivemobile.testcase;

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
import effectivemobile.testcase.task.service.TaskServiceImpl;
import effectivemobile.testcase.user.model.Role;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;
    private final Task task = new Task(1L, "Task Title", "Task Description",
            TaskStatus.PENDING, TaskPriority.HIGH, null, null, null);
    private final User creator = new User(1L, "test@test.ru", "ASDHASFKLUSHEDJKLSDH&*Y!#&*Y!@UASD",
            null, null, null, Role.ROLE_USER);
    private final User performer = new User(2L, "test1@test.ru", "ASDHASFKLUSHEDJKLSDH&*Y!#&*Y!@UASD",
            null, null, null, Role.ROLE_USER);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        task.setCreator(creator);
        task.setPerformer(performer);
    }

    @Test
    public void createTaskShouldReturnSavedTask() {
        NewTaskDto newTaskDto = new NewTaskDto("Task Title",
                "Task Description", TaskStatus.PENDING, TaskPriority.HIGH, 1L);
        Task task = TaskMapper.fromNewTask(newTaskDto);

        when(authService.getAuthenticatedUser()).thenReturn(creator);
        when(userService.findById(newTaskDto.getPerformerId())).thenReturn(performer);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.createTask(newTaskDto);

        assertEquals(newTaskDto.getTitle(), savedTask.getTitle());
        assertEquals(newTaskDto.getDescription(), savedTask.getDescription());
    }

    @Test
    public void getTaskByIdShouldReturnTask() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(task.getId());

        assertEquals(task.getId(), foundTask.getId());
    }

    @Test
    public void getTaskByIdShouldThrowNotFoundException() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.getTaskById(task.getId()));
    }

    @Test
    public void getAllTasksShouldReturnTasksList() {
        Task task2 = task;
        task2.setId(2L);
        when(taskRepository.findAll(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(List.of(task, task2));

        List<Task> tasks = taskService.getAllTasks("Task", TaskStatus.PENDING, TaskPriority.HIGH, 1L, 2L, 10);

        assertEquals(2, tasks.size());
    }

    @Test
    public void updateTaskStatusShouldUpdateStatus() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(creator);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTaskStatus(task.getId(), TaskStatus.COMPLETED);

        assertEquals(TaskStatus.COMPLETED, updatedTask.getStatus());
    }

    @Test
    public void updateTaskStatusShouldThrowForbiddenException() {
        User anotherUser = new User(3L, "another@test.ru",
                "password", "Another", "User", null, null);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(anotherUser);

        assertThrows(ForbiddenException.class, () -> taskService.updateTaskStatus(task.getId(), TaskStatus.COMPLETED));
    }

    @Test
    public void deleteTaskShouldDeleteTask() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(creator);

        taskService.deleteTask(task.getId());
    }

    @Test
    public void deleteTaskShouldThrowForbiddenException() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(performer);

        assertThrows(ForbiddenException.class, () -> taskService.deleteTask(task.getId()));
    }

    @Test
    public void editTaskShouldUpdateTask() {
        EditTaskDto editTaskDto = new EditTaskDto("New Title", "New Description",
                TaskStatus.IN_PROCESS, TaskPriority.LOW, 2L);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(creator);
        when(userService.findById(editTaskDto.getPerformerId())).thenReturn(performer);

        taskService.editTask(task.getId(), editTaskDto);

        assertEquals(editTaskDto.getTitle(), task.getTitle());
        assertEquals(editTaskDto.getDescription(), task.getDescription());
        assertEquals(editTaskDto.getStatus(), task.getStatus());
        assertEquals(editTaskDto.getPriority(), task.getPriority());
        assertEquals(editTaskDto.getPerformerId(), task.getPerformer().getId());
    }

    @Test
    public void editTaskShouldThrowForbiddenException() {
        EditTaskDto editTaskDto = new EditTaskDto("New Title", "New Description",
                TaskStatus.IN_PROCESS, TaskPriority.LOW, 2L);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(authService.getAuthenticatedUser()).thenReturn(performer);

        assertThrows(ForbiddenException.class, () -> taskService.editTask(task.getId(), editTaskDto));
    }
}

