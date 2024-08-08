package effectivemobile.testcase.comments.mapper;

import effectivemobile.testcase.comments.dto.CommentDto;
import effectivemobile.testcase.comments.dto.NewCommentDto;
import effectivemobile.testcase.comments.model.Comment;
import effectivemobile.testcase.user.mapper.UserMapper;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class CommentMapper {

    public static Comment fromNewCommentDto(NewCommentDto newCommentDto) {

        Comment comment = new Comment();
        comment.setComment(newCommentDto.getComment());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .taskId(comment.getTaskId())
                .user(UserMapper.toUserDto(comment.getAuthor()))
                .build();
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null) return Collections.emptyList();
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }
}
