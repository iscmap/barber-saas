package com.marioalba.booking.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marioalba.booking.application.AvailabilityDecision;
import com.marioalba.booking.application.BookingPersistenceService;
import com.marioalba.booking.application.BookingSagaOrchestrator;
import com.marioalba.common.events.EventEnvelope;
import com.marioalba.common.events.availability.AvailabilityDecidedV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@EnableScheduling
@Component
public class AvailabilityDecisionConsumer {
  private final SqsClient sqsClient;
  private final ObjectMapper objectMapper;
  private final String queueUrl;
  private final BookingPersistenceService bookingPersistenceService;

  public AvailabilityDecisionConsumer(
      SqsClient sqsClient,
      ObjectMapper objectMapper,
      BookingSagaOrchestrator orchestrator,
      BookingPersistenceService bookingPersistenceService,
      @Value("${messaging.sqs.resultsQueueUrl}") String queueUrl) {
    this.sqsClient = sqsClient;
    this.objectMapper = objectMapper;
    this.queueUrl = queueUrl;
    this.bookingPersistenceService = bookingPersistenceService;
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
      EventEnvelope<AvailabilityDecidedV1> envelope =
          objectMapper.readValue(
              body,
              objectMapper
                  .getTypeFactory()
                  .constructParametricType(EventEnvelope.class, AvailabilityDecidedV1.class));

      if (!"v1".equals(envelope.getVersion())) {
        System.err.println("Ignoring unsupported event version: " + envelope.getVersion());
        // delete message to avoid infinite retry in dev
        sqsClient.deleteMessage(
            DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(msg.receiptHandle())
                .build());
        continue;
      }

      if (!"AvailabilityDecided".equals(envelope.getEventType())) {
        continue;
      }

      if (envelope.getPayload() == null) {
        System.err.println("Invalid envelope (payload is null). Raw message: " + msg.body());

        // delete it so you don't loop forever
        sqsClient.deleteMessage(
            DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(msg.receiptHandle())
                .build());
        continue;
      }

      String bookingId = envelope.getPayload().getBookingId();

      AvailabilityDecision decision =
          AvailabilityDecision.fromWireValue(envelope.getPayload().getDecision());

      bookingPersistenceService.applyAvailabilityDecision(bookingId, decision);

      // Stub: just log for now; Step 3/5 will update Postgres booking status
      System.out.println("Received availability decision: " + envelope);

      sqsClient.deleteMessage(
          DeleteMessageRequest.builder()
              .queueUrl(queueUrl)
              .receiptHandle(msg.receiptHandle())
              .build());
    }
  }
}
