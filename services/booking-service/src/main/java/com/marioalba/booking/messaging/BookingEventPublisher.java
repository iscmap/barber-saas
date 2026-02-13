package com.marioalba.booking.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marioalba.booking.events.EventEnvelope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Component
public class BookingEventPublisher {
  private final SnsClient snsClient;
  private final ObjectMapper objectMapper;
  private final String topicArn;

  public BookingEventPublisher(
      SnsClient snsClient,
      ObjectMapper objectMapper,
      @Value("${messaging.sns.bookingsTopicArn}") String topicArn) {
    this.snsClient = snsClient;
    this.objectMapper = objectMapper;
    this.topicArn = topicArn;
  }

  public void publish(EventEnvelope<?> envelope) {
    try {
      String message = objectMapper.writeValueAsString(envelope);
      snsClient.publish(PublishRequest.builder().topicArn(topicArn).message(message).build());
    } catch (Exception e) {
      throw new RuntimeException("Failed to publish event", e);
    }
  }
}
