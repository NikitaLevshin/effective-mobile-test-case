package effectivemobile.testcase.comments.service;

import effectivemobile.testcase.comments.dto.NewCommentDto;
import effectivemobile.testcase.comments.model.Comment;

public interface CommentService {

    Comment addComment(long id, NewCommentDto newCommentDto);

    void deleteById(long id);
}
