package com.marioalba.availability.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marioalba.availability.events.AvailabilityDecisionV1;
import com.marioalba.availability.events.BookingCreatedV1;
import com.marioalba.availability.events.EventEnvelope;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@EnableScheduling
@Component
public class BookingCreatedConsumer {
  private final SqsClient sqsClient;
  private final SnsClient snsClient;
  private final ObjectMapper objectMapper;

  private final String queueUrl;
  private final String resultsTopicArn;

  public BookingCreatedConsumer(
      SqsClient sqsClient,
      SnsClient snsClient,
      ObjectMapper objectMapper,
      @Value("${messaging.sqs.bookingCreatedQueueUrl}") String queueUrl,
      @Value("${messaging.sns.availabilityResultsTopicArn}") String resultsTopicArn) {
    this.sqsClient = sqsClient;
    this.snsClient = snsClient;
    this.objectMapper = objectMapper;
    this.queueUrl = queueUrl;
    this.resultsTopicArn = resultsTopicArn;
  }

  @Scheduled(fixedDelay = 1000)
  public void poll() throws Exception {
    ReceiveMessageResponse resp =
        sqsClient.receiveMessage(
            ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(1)
                .build());

    for (Message msg : resp.messages()) {
      String body = SnsSqsMessageUnwrapper.unwrapIfNeeded(objectMapper, msg.body());
      // NOTE: SNS->SQS wraps your original message, but LocalStack usually passes it raw.
      // If you see wrapper JSON, we’ll unwrap in next step.
      EventEnvelope<BookingCreatedV1> envelope =
          objectMapper.readValue(
              body,
              objectMapper
                  .getTypeFactory()
                  .constructParametricType(EventEnvelope.class, BookingCreatedV1.class));

      if (envelope.getPayload() == null) {
        System.err.println("Invalid envelope (payload is null). Raw message: " + msg.body());
        // optionally delete message so it doesn't loop forever
        // or send to DLQ later; for now delete it to unblock:
        sqsClient.deleteMessage(
            DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(msg.receiptHandle())
                .build());
        continue;
      }

      // Stub: always RESERVED (real logic comes Step 5)
      AvailabilityDecisionV1 decision =
          AvailabilityDecisionV1.builder()
              .bookingId(envelope.getPayload().getBookingId())
              .decision("RESERVED")
              .reason(null)
              .build();

      EventEnvelope<AvailabilityDecisionV1> out =
          EventEnvelope.<AvailabilityDecisionV1>builder()
              .eventId(UUID.randomUUID().toString())
              .eventType("AvailabilityDecision")
              .version("v1")
              .occurredAt(Instant.now())
              .correlationId(envelope.getCorrelationId())
              .shopId(envelope.getShopId())
              .payload(decision)
              .build();

      snsClient.publish(
          PublishRequest.builder()
              .topicArn(resultsTopicArn)
              .message(objectMapper.writeValueAsString(out))
              .build());

      sqsClient.deleteMessage(
          DeleteMessageRequest.builder()
              .queueUrl(queueUrl)
              .receiptHandle(msg.receiptHandle())
              .build());
    }
  }
}
