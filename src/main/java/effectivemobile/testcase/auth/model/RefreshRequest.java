package effectivemobile.testcase.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Refresh токен, отправяемый при запросе на новый access или новую пару access/refresh")
public class RefreshRequest {

    private String refreshToken;

}
