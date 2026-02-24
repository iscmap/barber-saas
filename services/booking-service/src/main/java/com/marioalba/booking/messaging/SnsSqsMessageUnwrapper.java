package com.marioalba.booking.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SnsSqsMessageUnwrapper {

  private SnsSqsMessageUnwrapper() {}

  public static String unwrapIfNeeded(ObjectMapper objectMapper, String sqsBody) {
    try {
      JsonNode root = objectMapper.readTree(sqsBody);
      JsonNode messageNode = root.get("Message");
      if (messageNode != null && messageNode.isTextual()) {
        return messageNode.asText();
      }
      return sqsBody;
    } catch (Exception e) {
      return sqsBody;
    }
  }
}
