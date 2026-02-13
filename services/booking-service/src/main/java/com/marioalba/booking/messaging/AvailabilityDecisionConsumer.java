package com.marioalba.booking.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marioalba.booking.events.AvailabilityDecisionV1;
import com.marioalba.booking.events.EventEnvelope;
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

  public AvailabilityDecisionConsumer(
      SqsClient sqsClient,
      ObjectMapper objectMapper,
      @Value("${messaging.sqs.resultsQueueUrl}") String queueUrl) {
    this.sqsClient = sqsClient;
    this.objectMapper = objectMapper;
    this.queueUrl = queueUrl;
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
      EventEnvelope<AvailabilityDecisionV1> envelope =
          objectMapper.readValue(
              msg.body(),
              objectMapper
                  .getTypeFactory()
                  .constructParametricType(EventEnvelope.class, AvailabilityDecisionV1.class));

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
