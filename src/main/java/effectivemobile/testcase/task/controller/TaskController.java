package effectivemobile.testcase.task.controller;

import effectivemobile.testcase.task.dto.EditTaskDto;
import effectivemobile.testcase.task.dto.NewTaskDto;
import effectivemobile.testcase.task.dto.TaskDto;
import effectivemobile.testcase.task.mapper.TaskMapper;
import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;
import effectivemobile.testcase.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Задачи", description = "Взаимодействие с задачами")
@SecurityRequirement(name = "JWT")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание нового задания")
    public TaskDto createTask(@Valid @RequestBody NewTaskDto newTaskDto) {
        log.info("Request to create task: {}", newTaskDto);
        return TaskMapper.toTaskDto(taskService.createTask(newTaskDto));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение всех заданий с возможностью фильтрации")
    public List<TaskDto> getAllTasks(@RequestParam(required = false) String title,
                                     @RequestParam(required = false) TaskStatus status,
                                     @RequestParam(required = false) TaskPriority priority,
                                     @RequestParam(required = false) Long creator,
                                     @RequestParam(required = false) Long performer,
                                     @RequestParam(defaultValue = "20") Integer size) {
        log.info("Request to get all tasks");
        return taskService.getAllTasks(title, status, priority, creator, performer, size).stream()
                .map(TaskMapper::toTaskDto)
                .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение конкретного задания по id")
    public TaskDto getTaskById(@PathVariable long id) {
        log.info("Request to get task by id: {}", id);
        return TaskMapper.toTaskDto(taskService.getTaskById(id));
    }

    @PatchMapping("/status/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновление статуса задания")
    public TaskDto updateTaskStatus(@PathVariable long id, @RequestBody TaskStatus taskStatus) {
        log.info("Request to update task status with id: {}", id);
        return TaskMapper.toTaskDto(taskService.updateTaskStatus(id, taskStatus));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление задания")
    public void deleteTask(@PathVariable long id) {
        log.info("Request to delete task by id: {}", id);
        taskService.deleteTask(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Редактирование существующего задания")
    public TaskDto editTask(@PathVariable long id, @RequestBody EditTaskDto editTaskDto) {
        log.info("Request to edit task by id: {}", id);
        return TaskMapper.toTaskDto(taskService.editTask(id, editTaskDto));
    }
}
