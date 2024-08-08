package effectivemobile.testcase.comments.service;

import effectivemobile.testcase.auth.service.AuthService;
import effectivemobile.testcase.comments.dto.NewCommentDto;
import effectivemobile.testcase.comments.mapper.CommentMapper;
import effectivemobile.testcase.comments.model.Comment;
import effectivemobile.testcase.comments.repository.CommentRepository;
import effectivemobile.testcase.exceptions.ForbiddenException;
import effectivemobile.testcase.exceptions.NotFoundException;
import effectivemobile.testcase.task.model.Task;
import effectivemobile.testcase.task.service.TaskService;
import effectivemobile.testcase.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final TaskService taskService;

    @Override
    public Comment addComment(long id, NewCommentDto newCommentDto) {
        log.info("addComment");
        Task task = taskService.getTaskById(id);
        long creatorId = task.getCreator().getId();
        long performerId = 0;
        if (task.getPerformer() != null) {
            performerId = task.getPerformer().getId();
        }
        User user = authService.getAuthenticatedUser();
        if (user.getId() != creatorId && user.getId() != performerId) {
            throw new ForbiddenException("У вас нет прав на добавление комментария к этой задаче");
        }
        Comment comment = CommentMapper.fromNewCommentDto(newCommentDto);
        comment.setTaskId(id);
        comment.setAuthor(user);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteById(long id) {
        log.info("deleting comment with id {}", id);
        Comment comment = commentRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (comment.getAuthor().getId() != authService.getAuthenticatedUser().getId()) {
            throw new ForbiddenException("У вас нет прав на удаление комментария");
        }
        commentRepository.deleteById(id);
    }
}
