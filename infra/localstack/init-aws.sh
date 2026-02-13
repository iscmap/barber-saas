#!/usr/bin/env bash
set -euo pipefail

echo "Creating LocalStack resources..."

# SNS topic
awslocal sns create-topic --name bookings

# SQS queues + DLQ
awslocal sqs create-queue --queue-name availability-booking-created-dlq
DLQ_URL=$(awslocal sqs get-queue-url --queue-name availability-booking-created-dlq --query 'QueueUrl' --output text)
DLQ_ARN=$(awslocal sqs get-queue-attributes --queue-url "$DLQ_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sqs create-queue \
  --queue-name availability-booking-created \
  --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"$DLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\"}"

# Subscribe queue to topic (fanout)
Q_URL=$(awslocal sqs get-queue-url --queue-name availability-booking-created --query 'QueueUrl' --output text)
Q_ARN=$(awslocal sqs get-queue-attributes --queue-url "$Q_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

# Allow SNS to send messages to SQS
awslocal sqs set-queue-attributes \
  --queue-url "$Q_URL" \
  --attributes "{\"Policy\":\"{\\\"Version\\\":\\\"2012-10-17\\\",\\\"Statement\\\":[{\\\"Sid\\\":\\\"Allow-SNS-SendMessage\\\",\\\"Effect\\\":\\\"Allow\\\",\\\"Principal\\\":\\\"*\\\",\\\"Action\\\":\\\"SQS:SendMessage\\\",\\\"Resource\\\":\\\"$Q_ARN\\\",\\\"Condition\\\":{\\\"ArnEquals\\\":{\\\"aws:SourceArn\\\":\\\"arn:aws:sns:us-east-1:000000000000:bookings\\\"}}}]}\"}"

awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:bookings --protocol sqs --notification-endpoint "$Q_ARN"

# DynamoDB tables (placeholders for Step 5)
awslocal dynamodb create-table \
  --table-name shop_daily_barbers \
  --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
  --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  >/dev/null 2>&1 || true

awslocal dynamodb create-table \
  --table-name availability_reservations \
  --attribute-definitions AttributeName=bookingId,AttributeType=S \
  --key-schema AttributeName=bookingId,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  >/dev/null 2>&1 || true

echo "LocalStack resources created."

awslocal dynamodb create-table \
  --table-name availability_slot_locks \
  --attribute-definitions AttributeName=pk,AttributeType=S AttributeName=sk,AttributeType=S \
  --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST \
  >/dev/null 2>&1 || true

# SNS topic for availability results
awslocal sns create-topic --name availability-results

# booking-service results queue + DLQ
awslocal sqs create-queue --queue-name booking-availability-results-dlq
RDLQ_URL=$(awslocal sqs get-queue-url --queue-name booking-availability-results-dlq --query 'QueueUrl' --output text)
RDLQ_ARN=$(awslocal sqs get-queue-attributes --queue-url "$RDLQ_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

awslocal sqs create-queue \
  --queue-name booking-availability-results \
  --attributes "{\"RedrivePolicy\":\"{\\\"deadLetterTargetArn\\\":\\\"$RDLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"5\\\"}\"}"

# Subscribe booking-service results queue to availability-results topic
RQ_URL=$(awslocal sqs get-queue-url --queue-name booking-availability-results --query 'QueueUrl' --output text)
RQ_ARN=$(awslocal sqs get-queue-attributes --queue-url "$RQ_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

# Allow SNS to send messages to the results queue
awslocal sqs set-queue-attributes \
  --queue-url "$RQ_URL" \
  --attributes "{\"Policy\":\"{\\\"Version\\\":\\\"2012-10-17\\\",\\\"Statement\\\":[{\\\"Sid\\\":\\\"Allow-SNS-SendMessage\\\",\\\"Effect\\\":\\\"Allow\\\",\\\"Principal\\\":\\\"*\\\",\\\"Action\\\":\\\"SQS:SendMessage\\\",\\\"Resource\\\":\\\"$RQ_ARN\\\",\\\"Condition\\\":{\\\"ArnEquals\\\":{\\\"aws:SourceArn\\\":\\\"arn:aws:sns:us-east-1:000000000000:availability-results\\\"}}}]}\"}"

awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:availability-results --protocol sqs --notification-endpoint "$RQ_ARN"
