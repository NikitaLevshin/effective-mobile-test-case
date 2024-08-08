package effectivemobile.testcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import effectivemobile.testcase.auth.service.AuthService;
import effectivemobile.testcase.task.controller.TaskController;
import effectivemobile.testcase.task.dto.EditTaskDto;
import effectivemobile.testcase.task.dto.NewTaskDto;
import effectivemobile.testcase.task.model.Task;
import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;
import effectivemobile.testcase.task.service.TaskService;
import effectivemobile.testcase.user.model.Role;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest {

    private MockMvc mockMvc;


    @Mock
    private TaskService taskService;

    @Mock

    private UserService userService;
    @Mock

    private AuthService authService;

    @InjectMocks
    private TaskController taskController;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final User creator = new User(1L, "test@test.ru", "ASDHASFKLUSHEDJKLSDH&*Y!#&*Y!@UASD",
            null, null, null, Role.ROLE_USER);
    private final User performer = new User(2L, "test1@test.ru", "ASDHASFKLUSHEDJKLSDH&*Y!#&*Y!@UASD",
            null, null, null, Role.ROLE_USER);
    private final Task task = new Task();


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        task.setId(1);
        task.setTitle("Test Task");
        task.setDescription("This is a test task description.");
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(TaskPriority.HIGH);
        task.setCreator(creator);
        task.setPerformer(performer);
    }

    @Test
    public void createTaskShouldReturnCreatedTask() throws Exception {
        NewTaskDto newTaskDto = new NewTaskDto();
        newTaskDto.setTitle("Test Task");
        newTaskDto.setDescription("This is a test task description.");
        newTaskDto.setStatus(TaskStatus.PENDING);
        newTaskDto.setPriority(TaskPriority.HIGH);
        newTaskDto.setPerformerId(1L);

        when(taskService.createTask(any())).thenReturn(task);
        when(userService.findById(performer.getId())).thenReturn(performer);
        when(authService.getAuthenticatedUser()).thenReturn(creator);

        mockMvc.perform(post("/task")
                        .content(objectMapper.writeValueAsString(newTaskDto))
                        .contentType(MediaType.APPLICATION_JSON))


                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.creator.id").value(creator.getId()))
                .andExpect(jsonPath("$.performer.id").value(performer.getId()));

    }

    @Test
    public void getAllTasksShouldReturnListOfTasks() throws Exception {
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Test Task 2");
        task2.setDescription("Description for task 2");
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setPriority(TaskPriority.LOW);
        task2.setCreator(new User());
        task2.setPerformer(new User());

        when(taskService.getAllTasks(any(), any(), any(), any(), any(), eq(20)))
                .thenReturn(List.of(task, task2));

        mockMvc.perform(get("/task")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value(task.getTitle()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value(task2.getTitle()));

    }

    @Test
    public void getTaskByIdShouldReturnTask() throws Exception {


        when(taskService.getTaskById(task.getId())).thenReturn(task);

        mockMvc.perform(get("/task/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.creator.id").value(creator.getId()))
                .andExpect(jsonPath("$.performer.id").value(performer.getId()));

    }

    @Test
    public void updateTaskStatusShouldReturnUpdatedTask() throws Exception {
        TaskStatus taskStatus = TaskStatus.IN_PROCESS;
        task.setStatus(taskStatus);

        when(taskService.updateTaskStatus(eq(task.getId()), eq(taskStatus))).thenReturn(task);

        mockMvc.perform(patch("/task/status/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.status").value("IN_PROCESS"));

    }

    @Test
    public void deleteTaskShouldReturnNoContent() throws Exception {
        long taskId = 1L;

        mockMvc.perform(delete("/task/{id}", taskId))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(taskId);
    }

    @Test
    public void editTaskShouldReturnEditedTask() throws Exception {

        EditTaskDto editTaskDto = new EditTaskDto();
        editTaskDto.setTitle("Updated Task Title");
        editTaskDto.setDescription("Updated task description.");
        editTaskDto.setStatus(TaskStatus.IN_PROCESS);
        editTaskDto.setPriority(TaskPriority.MEDIUM);

        task.setTitle(editTaskDto.getTitle());
        task.setDescription(editTaskDto.getDescription());
        task.setStatus(editTaskDto.getStatus());
        task.setPriority(editTaskDto.getPriority());
        task.setCreator(creator);
        task.setPerformer(performer);

        when(taskService.editTask(eq(task.getId()), any(EditTaskDto.class))).thenReturn(task);

        mockMvc.perform(patch("/task/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.status").value("IN_PROCESS"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));

    }
}
