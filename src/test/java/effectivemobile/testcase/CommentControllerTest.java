package effectivemobile.testcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import effectivemobile.testcase.comments.controller.CommentController;
import effectivemobile.testcase.comments.dto.NewCommentDto;
import effectivemobile.testcase.comments.model.Comment;
import effectivemobile.testcase.comments.service.CommentService;
import effectivemobile.testcase.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    public void createCommentShouldReturnCreatedComment() throws Exception {
        long taskId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setComment("Test comment");

        Comment createdComment = new Comment();
        createdComment.setComment("Test comment");
        createdComment.setTaskId(taskId);
        createdComment.setAuthor(new User());

        when(commentService.addComment(eq(taskId), any(NewCommentDto.class))).thenReturn(createdComment);

        mockMvc.perform(post("/task/{taskId}/comment", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))  // Замените на JSON, соответствующий newCommentDto
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value("Test comment"))
                .andExpect(jsonPath("$.taskId").value(taskId));
    }

    @Test
    public void deleteCommentByIdShouldReturnNoContent() throws Exception {
        long commentId = 1L;

        mockMvc.perform(delete("/comment/{id}", commentId))
                .andExpect(status().isNoContent());
    }
}
