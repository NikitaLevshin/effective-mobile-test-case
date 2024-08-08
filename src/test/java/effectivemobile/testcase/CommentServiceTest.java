package effectivemobile.testcase;

import effectivemobile.testcase.auth.service.AuthService;
import effectivemobile.testcase.comments.dto.NewCommentDto;
import effectivemobile.testcase.comments.mapper.CommentMapper;
import effectivemobile.testcase.comments.model.Comment;
import effectivemobile.testcase.comments.repository.CommentRepository;
import effectivemobile.testcase.comments.service.CommentServiceImpl;
import effectivemobile.testcase.exceptions.ForbiddenException;
import effectivemobile.testcase.exceptions.NotFoundException;
import effectivemobile.testcase.task.model.Task;
import effectivemobile.testcase.task.service.TaskService;
import effectivemobile.testcase.user.model.Role;
import effectivemobile.testcase.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AuthService authService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentServiceImpl commentService;
    private final NewCommentDto newCommentDto = new NewCommentDto("This is a test comment");
    private final User creator = new User(1L, "test@test.ru", "ASDHASFKLUSHEDJKLSDH&*Y!#&*Y!@UASD",
            null, null, null, Role.ROLE_USER);
    private final User performer = new User(2L, "test1@test.ru", "ASDHASFKLUSHEDJKLSDH&*Y!#&*Y!@UASD",
            null, null, null, Role.ROLE_USER);
    private final long commentId = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addCommentShouldReturnSavedComment() {
        long taskId = 1L;
        Task task = new Task();
        User user = creator;

        task.setCreator(creator);
        task.setPerformer(creator);
        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(authService.getAuthenticatedUser()).thenReturn(user);

        Comment comment = CommentMapper.fromNewCommentDto(newCommentDto);
        comment.setTaskId(taskId);
        comment.setAuthor(user);

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment savedComment = commentService.addComment(taskId, newCommentDto);

        assertEquals(newCommentDto.getComment(), savedComment.getComment());
        assertEquals(taskId, savedComment.getTaskId());
        assertEquals(user, savedComment.getAuthor());
    }

    @Test
    public void addCommentShouldThrowForbiddenExceptionWhenUserNotCreatorOrPerformer() {
        long taskId = 1L;
        Task task = new Task();
        task.setCreator(creator);
        task.setPerformer(performer);

        User anotherUser = new User(3L, "another@test.ru", null, null,
                null, null, Role.ROLE_USER);

        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(authService.getAuthenticatedUser()).thenReturn(anotherUser);

        assertThrows(ForbiddenException.class, () -> commentService.addComment(taskId, newCommentDto));
    }

    @Test
    public void deleteByIdShouldDeleteComment() {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(creator);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(authService.getAuthenticatedUser()).thenReturn(creator);

        commentService.deleteById(commentId);
    }

    @Test
    public void deleteByIdShouldThrowNotFoundExceptionWhenCommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.deleteById(commentId));
    }

    @Test
    public void deleteByIdShouldThrowForbiddenException_WhenUserNotAuthor() {
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setAuthor(creator);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(authService.getAuthenticatedUser()).thenReturn(performer);

        assertThrows(ForbiddenException.class, () -> commentService.deleteById(commentId));
    }
}
