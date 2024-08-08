package effectivemobile.testcase.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(ForbiddenException e) {
        log.error("Forbidden exception caught: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Ошибка доступа")
                .status(HttpStatus.FORBIDDEN.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.error("Not found exception caught: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Контент не найден")
                .status(HttpStatus.NOT_FOUND.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation exception caught: {}", Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
        return ApiError.builder()
                .message(Objects.requireNonNull(e.getFieldError()).getDefaultMessage())
                .reason("Ошибка валидации")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(ConstraintViolationException e) {
        log.error("Validation exception caught: {}", e.getConstraintViolations().iterator().next().getMessage());
        return ApiError.builder()
                .message(e.getConstraintViolations().iterator().next().getMessage())
                .reason("Ошибка валидации")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerConstraint(final DataIntegrityViolationException e) {
        log.debug("Получена ошибка валидации 400 {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Ошибка валидации")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlerHttpMessageNotReadable(final HttpMessageNotReadableException e) {
        log.debug("Получена ошибка валидации 400: Ошибка преобразования enum");
        return ApiError.builder()
                .message("Такого статуса/приоритета не существует")
                .reason("Ошибка валидации")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleJwtException(JwtTokenException e) {
        log.debug("Получена ошибка валидации 400 {}", e.getMessage(), e);
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Ошибка токена")
                .status(HttpStatus.FORBIDDEN.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAuthException(AuthException e) {
        log.error("Auth exception caught: {}", e.getMessage());
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Ошибка авторизации")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
