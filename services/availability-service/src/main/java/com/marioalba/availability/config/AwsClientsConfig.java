package com.marioalba.availability.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsClientsConfig {

  private StaticCredentialsProvider creds() {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"));
  }

  @Bean
  public SnsClient snsClient(
      @Value("${aws.region}") String region, @Value("${aws.endpoint}") String endpoint) {
    return SnsClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint))
        .credentialsProvider(creds())
        .build();
  }

  @Bean
  public SqsClient sqsClient(
      @Value("${aws.region}") String region, @Value("${aws.endpoint}") String endpoint) {
    return SqsClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint))
        .credentialsProvider(creds())
        .build();
  }

  @Bean
  public DynamoDbClient dynamoDbClient(
      @Value("${aws.region}") String region, @Value("${aws.dynamodb.endpoint}") String endpoint) {
    return DynamoDbClient.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint))
        .credentialsProvider(creds())
        .build();
  }
}
