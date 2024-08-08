package effectivemobile.testcase.task.service;


import effectivemobile.testcase.task.dto.EditTaskDto;
import effectivemobile.testcase.task.dto.NewTaskDto;
import effectivemobile.testcase.task.model.Task;
import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;

import java.util.List;

public interface TaskService {

    Task createTask(NewTaskDto newTaskDto);

    Task getTaskById(long id);

    List<Task> getAllTasks(java.lang.String title, TaskStatus taskStatus, TaskPriority taskPriority, Long creator,
                           Long performer, Integer size);


    Task updateTaskStatus(long id, TaskStatus taskStatus);

    void deleteTask(long id);

    Task editTask(long id, EditTaskDto editTaskDto);
}
