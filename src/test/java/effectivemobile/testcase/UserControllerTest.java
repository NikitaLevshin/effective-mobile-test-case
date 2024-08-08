package effectivemobile.testcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import effectivemobile.testcase.user.controller.UserController;
import effectivemobile.testcase.user.dto.EditUserDto;
import effectivemobile.testcase.user.model.Role;
import effectivemobile.testcase.user.model.User;
import effectivemobile.testcase.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;
    ObjectMapper objectMapper = new ObjectMapper();
    private final User user = new User(1L, "test@test.ru", "DFHSDKLJFHAS", "test",
            "user", null, Role.ROLE_USER);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void getUserByIdShouldReturnUser() throws Exception {
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/user/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()));
    }

    @Test
    public void getAllUsersShouldReturnListOfUsers() throws Exception {
        User user2 = new User(2L, "test1@test.ru", "test1", "user1",
                null, null, Role.ROLE_USER);

        when(userService.findAll()).thenReturn(List.of(user, user2));


        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()))
                .andExpect(jsonPath("$[1].firstName").value(user2.getFirstName()));

        verify(userService).findAll();
    }

    @Test
    public void updateUserShouldReturnUpdatedUser() throws Exception {
        long userId = 1L;
        EditUserDto editUserDto = new EditUserDto("newemail@test.ru", "NewTestName", "NewTestLast");
        user.setEmail(editUserDto.getEmail());
        user.setFirstName(editUserDto.getFirstName());
        user.setLastName(editUserDto.getLastName());

        when(userService.update(any(EditUserDto.class))).thenReturn(user);

        mockMvc.perform(patch("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()));

        verify(userService).update(any(EditUserDto.class));
    }
}

