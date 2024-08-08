package effectivemobile.testcase.comments.controller;

import effectivemobile.testcase.comments.dto.CommentDto;
import effectivemobile.testcase.comments.dto.NewCommentDto;
import effectivemobile.testcase.comments.mapper.CommentMapper;
import effectivemobile.testcase.comments.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Комментарии", description = "Добавление и удаление комментариев")
@SecurityRequirement(name = "JWT")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("task/{taskId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление нового комментария к заданию")
    public CommentDto createComment(@PathVariable long taskId, @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Request to create comment for task {} with new comment", taskId);
        return CommentMapper.toCommentDto(commentService.addComment(taskId, newCommentDto));
    }

    @DeleteMapping("comment/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление комментария")
    public void deleteCommentById(@PathVariable long id) {
        log.info("Request to delete comment with id {}", id);
        commentService.deleteById(id);
    }
}
