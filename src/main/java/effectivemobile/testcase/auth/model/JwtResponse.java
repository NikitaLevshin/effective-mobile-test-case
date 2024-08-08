package effectivemobile.testcase.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Объект, возвращающий access и refresh токены при успешной авторизации")
public class JwtResponse {

    private String accessToken;
    private String refreshToken;

}
