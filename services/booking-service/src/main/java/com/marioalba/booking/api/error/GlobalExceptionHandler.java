package com.marioalba.booking.api.error;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ProblemResponse.FieldError> errors =
        ex.getBindingResult().getFieldErrors().stream().map(this::toFieldError).toList();

    ProblemResponse body =
        ProblemResponse.builder()
            .type("https://api.marioalba.com/problems/validation-error")
            .title("Validation failed")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail("One or more fields are invalid")
            .instance(request.getRequestURI())
            .correlationId(correlationIdOrNull(request))
            .errors(errors)
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(PROBLEM_JSON).body(body);
  }

  private ProblemResponse.FieldError toFieldError(FieldError fe) {
    return ProblemResponse.FieldError.builder()
        .field(fe.getField())
        .message(fe.getDefaultMessage())
        .build();
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ProblemResponse> handleMissingHeader(
      MissingRequestHeaderException ex, HttpServletRequest request) {

    ProblemResponse body =
        ProblemResponse.builder()
            .type("https://api.marioalba.com/problems/validation-error")
            .title("Missing required header")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail(ex.getMessage())
            .instance(request.getRequestURI())
            .correlationId(correlationIdOrNull(request))
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(PROBLEM_JSON).body(body);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    ProblemResponse body =
        ProblemResponse.builder()
            .type("https://api.marioalba.com/problems/validation-error")
            .title("Invalid parameter format")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail("Invalid value for parameter '" + ex.getName() + "'")
            .instance(request.getRequestURI())
            .correlationId(correlationIdOrNull(request))
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(PROBLEM_JSON).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemResponse> handleGeneric(Exception ex, HttpServletRequest request) {

    ProblemResponse body =
        ProblemResponse.builder()
            .type("https://api.marioalba.com/problems/internal-error")
            .title("Internal server error")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .detail("Unexpected error")
            .instance(request.getRequestURI())
            .correlationId(correlationIdOrNull(request))
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(PROBLEM_JSON)
        .body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemResponse> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    ProblemResponse body =
        ProblemResponse.builder()
            .type("https://api.marioalba.com/problems/validation-error")
            .title("Malformed JSON request")
            .status(HttpStatus.BAD_REQUEST.value())
            .detail("Request body is not valid JSON or has invalid field formats")
            .instance(request.getRequestURI())
            .correlationId(correlationIdOrNull(request))
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(PROBLEM_JSON).body(body);
  }

  private String correlationIdOrNull(HttpServletRequest request) {
    // If you haven't added CorrelationIdFilter yet, this will just be null.
    // Later we’ll standardize correlation id via filter + MDC.
    return request.getHeader("X-Correlation-Id");
  }
}
