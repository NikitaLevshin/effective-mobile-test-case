package effectivemobile.testcase.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {

    private String message;
    private String reason;
    private String status;
    private LocalDateTime timeStamp;

}
