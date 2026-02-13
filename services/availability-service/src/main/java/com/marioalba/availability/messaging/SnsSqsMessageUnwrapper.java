package com.marioalba.availability.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SnsSqsMessageUnwrapper {
  private SnsSqsMessageUnwrapper() {}

  public static String unwrapIfNeeded(ObjectMapper objectMapper, String sqsBody) {
    try {
      JsonNode root = objectMapper.readTree(sqsBody);

      // SNS->SQS notifications contain a top-level "Message" field with the real payload as a
      // string
      JsonNode messageNode = root.get("Message");
      if (messageNode != null && messageNode.isTextual()) {
        return messageNode.asText();
      }

      // If it's already the real payload, return as-is
      return sqsBody;

    } catch (Exception e) {
      // If it's not JSON, just return raw
      return sqsBody;
    }
  }
}
