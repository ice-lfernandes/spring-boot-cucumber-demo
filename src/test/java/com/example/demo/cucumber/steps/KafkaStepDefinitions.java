package com.example.demo.cucumber.steps;

import com.example.demo.kafka.KafkaConsumerService;
import com.example.demo.kafka.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for Kafka Messaging feature.
 * Contains the implementation of Gherkin steps for Kafka publish/consume scenarios.
 */
public class KafkaStepDefinitions {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    private String currentMessage;
    private String currentKey;
    private CompletableFuture<SendResult<String, String>> sendResult;
    private boolean messagePublished;

    @Before("@kafka")
    public void setup() {
        kafkaConsumerService.clearMessages();
        currentMessage = null;
        currentKey = null;
        messagePublished = false;
    }

    @After("@kafka")
    public void cleanup() {
        kafkaConsumerService.clearMessages();
    }

    @Given("the Kafka service is running")
    public void theKafkaServiceIsRunning() {
        assertNotNull(kafkaProducerService, "Kafka Producer Service should be available");
        assertNotNull(kafkaConsumerService, "Kafka Consumer Service should be available");
    }

    @Given("I have a message with content {string}")
    public void iHaveAMessageWithContent(String content) {
        currentMessage = content;
    }

    @Given("I have a message with key {string} and content {string}")
    public void iHaveAMessageWithKeyAndContent(String key, String content) {
        currentKey = key;
        currentMessage = content;
    }

    @Given("I have a user event with name {string} and email {string}")
    public void iHaveAUserEventWithNameAndEmail(String name, String email) {
        ObjectNode userEvent = objectMapper.createObjectNode();
        userEvent.put("type", "USER_CREATED");
        userEvent.put("name", name);
        userEvent.put("email", email);
        currentMessage = userEvent.toString();
    }

    @When("I publish the message to topic {string}")
    public void iPublishTheMessageToTopic(String topic) throws Exception {
        kafkaConsumerService.resetLatch();
        sendResult = kafkaProducerService.publishMessage(topic, currentMessage);
        sendResult.get(10, TimeUnit.SECONDS);
        messagePublished = true;
    }

    @When("I publish the message with key to topic {string}")
    public void iPublishTheMessageWithKeyToTopic(String topic) throws Exception {
        kafkaConsumerService.resetLatch();
        sendResult = kafkaProducerService.publishMessage(topic, currentKey, currentMessage);
        sendResult.get(10, TimeUnit.SECONDS);
        messagePublished = true;
    }

    @When("I publish the user event to topic {string}")
    public void iPublishTheUserEventToTopic(String topic) throws Exception {
        kafkaConsumerService.resetLatch();
        sendResult = kafkaProducerService.publishMessage(topic, currentMessage);
        sendResult.get(10, TimeUnit.SECONDS);
        messagePublished = true;
    }

    @When("I publish the following messages to topic {string}:")
    public void iPublishTheFollowingMessagesToTopic(String topic, DataTable dataTable) throws Exception {
        List<Map<String, String>> messages = dataTable.asMaps(String.class, String.class);
        kafkaConsumerService.resetLatch(messages.size());
        
        for (Map<String, String> row : messages) {
            String message = row.get("message");
            kafkaProducerService.publishMessage(topic, message).get(10, TimeUnit.SECONDS);
        }
        messagePublished = true;
    }

    @Then("the message should be published successfully")
    public void theMessageShouldBePublishedSuccessfully() {
        assertTrue(messagePublished, "Message should be published successfully");
        assertNotNull(sendResult, "Send result should not be null");
    }

    @Then("the consumer should receive the message {string}")
    public void theConsumerShouldReceiveTheMessage(String expectedMessage) throws InterruptedException {
        boolean received = kafkaConsumerService.waitForMessage(10, TimeUnit.SECONDS);
        assertTrue(received, "Consumer should receive message within timeout");
        
        String lastMessage = kafkaConsumerService.getLastReceivedMessage();
        assertNotNull(lastMessage, "Consumer should have received a message");
        assertEquals(expectedMessage, lastMessage, "Received message should match expected");
    }

    @And("the consumer should receive a message containing {string}")
    public void theConsumerShouldReceiveAMessageContaining(String expectedContent) throws InterruptedException {
        boolean received = kafkaConsumerService.waitForMessage(10, TimeUnit.SECONDS);
        assertTrue(received, "Consumer should receive message within timeout");
        
        String lastMessage = kafkaConsumerService.getLastReceivedMessage();
        assertNotNull(lastMessage, "Consumer should have received a message");
        assertTrue(lastMessage.contains(expectedContent), 
                "Received message should contain: " + expectedContent);
    }

    @Then("the consumer should receive {int} messages")
    public void theConsumerShouldReceiveMessages(int expectedCount) throws InterruptedException {
        // Poll until expected count is reached or timeout
        long timeoutMs = 15000;
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (kafkaConsumerService.getMessageCount() >= expectedCount) {
                break;
            }
            kafkaConsumerService.waitForMessage(500, TimeUnit.MILLISECONDS);
        }
        
        int actualCount = kafkaConsumerService.getMessageCount();
        assertEquals(expectedCount, actualCount, 
                "Consumer should receive " + expectedCount + " messages");
    }

    @Then("the consumer should receive messages in order:")
    public void theConsumerShouldReceiveMessagesInOrder(DataTable dataTable) throws InterruptedException {
        List<Map<String, String>> expectedMessages = dataTable.asMaps(String.class, String.class);
        int expectedCount = expectedMessages.size();
        
        // Poll until expected count is reached or timeout
        long timeoutMs = 15000;
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (kafkaConsumerService.getMessageCount() >= expectedCount) {
                break;
            }
            kafkaConsumerService.waitForMessage(500, TimeUnit.MILLISECONDS);
        }
        
        List<String> actualMessages = kafkaConsumerService.getReceivedMessages();
        assertEquals(expectedMessages.size(), actualMessages.size(), 
                "Number of messages should match");
        
        for (int i = 0; i < expectedMessages.size(); i++) {
            String expected = expectedMessages.get(i).get("message");
            String actual = actualMessages.get(i);
            assertEquals(expected, actual, "Message at position " + i + " should match");
        }
    }
}
