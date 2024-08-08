package effectivemobile.testcase.comments.model;

import effectivemobile.testcase.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
@Schema(description = "Сущность комментария, хранящаяся в БД")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "Комментарий не может быть пустым")
    @Size(min = 5, max = 1000, message = "Комментарий не должен быть короче 5 символов, и длинее 1000")
    private String comment;

    private long taskId;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}
