package effectivemobile.testcase.task.model;

import effectivemobile.testcase.comments.model.Comment;
import effectivemobile.testcase.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
@Schema(description = "Сущность задачи, хранящаяся в БД")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotEmpty(message = "Название не может быть пустым")
    private String title;
    @Size(min = 10, max = 500, message = "Описание должно быть не короче 10 и не больше 500 символов")
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "performer_id")
    private User performer;
    @OneToMany(mappedBy = "taskId")
    private List<Comment> comments;
}
