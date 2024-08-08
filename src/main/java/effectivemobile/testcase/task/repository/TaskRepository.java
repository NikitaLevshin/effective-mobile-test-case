package effectivemobile.testcase.task.repository;

import effectivemobile.testcase.task.model.Task;
import effectivemobile.testcase.task.model.TaskPriority;
import effectivemobile.testcase.task.model.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t " +
            "WHERE ((lower(t.title) LIKE concat('%', lower(:title),'%') OR :title IS NULL) " +
            "AND (t.status = :status OR :status IS NULL) AND (t.priority = :priority OR :priority IS NULL) AND " +
            "(t.creator.id = :creator OR :creator IS NULL) AND (t.performer.id = :performer OR :performer IS NULL))")
    List<Task> findAll(java.lang.String title, TaskStatus status, TaskPriority priority, Long creator,
                       Long performer, Pageable pageable);
}
