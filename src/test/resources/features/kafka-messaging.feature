Feature: Kafka Messaging
  As a message producer and consumer
  I want to publish and consume messages through Kafka
  So that I can verify asynchronous message processing

  Background:
    Given the Kafka service is running

  Scenario: Publish a message to Kafka topic
    Given I have a message with content "Hello Kafka!"
    When I publish the message to topic "user-events"
    Then the message should be published successfully

  Scenario: Consume a message from Kafka topic
    Given I have a message with content "User created event"
    When I publish the message to topic "user-events"
    Then the consumer should receive the message "User created event"

  Scenario: Publish user event to Kafka
    Given I have a user event with name "John Doe" and email "john@example.com"
    When I publish the user event to topic "user-events"
    Then the message should be published successfully
    And the consumer should receive a message containing "John Doe"

  Scenario: Publish multiple messages to Kafka
    When I publish the following messages to topic "user-events":
      | message                  |
      | First message            |
      | Second message           |
      | Third message            |
    Then the consumer should receive 3 messages

  Scenario: Publish message with key to Kafka topic
    Given I have a message with key "user-123" and content "User update event"
    When I publish the message with key to topic "user-events"
    Then the message should be published successfully
    And the consumer should receive the message "User update event"

  Scenario: Verify message order in Kafka
    When I publish the following messages to topic "user-events":
      | message                  |
      | Event 1                  |
      | Event 2                  |
    Then the consumer should receive messages in order:
      | message                  |
      | Event 1                  |
      | Event 2                  |
