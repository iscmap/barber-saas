package com.marioalba.booking.api.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemResponse {
  String type;
  String title;
  Integer status;
  String detail;
  String instance;
  String correlationId;

  List<FieldError> errors;

  @Value
  @Builder
  public static class FieldError {
    String field;
    String message;
  }
}
