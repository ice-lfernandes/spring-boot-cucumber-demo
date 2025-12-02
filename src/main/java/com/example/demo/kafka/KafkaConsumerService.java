package com.example.demo.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Kafka Consumer Service for consuming messages from Kafka topics.
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final List<String> receivedMessages = new CopyOnWriteArrayList<>();
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * Kafka listener that consumes messages from the "user-events" topic.
     *
     * @param message the received message
     */
    @KafkaListener(topics = "${kafka.topic.user-events:user-events}", groupId = "${spring.kafka.consumer.group-id:cucumber-demo-group}")
    public void consumeUserEvent(String message) {
        logger.info("Received message: {}", message);
        receivedMessages.add(message);
        latch.countDown();
    }

    /**
     * Gets all received messages.
     *
     * @return list of received messages
     */
    public List<String> getReceivedMessages() {
        return new ArrayList<>(receivedMessages);
    }

    /**
     * Gets the last received message.
     *
     * @return the last received message or null if no messages
     */
    public String getLastReceivedMessage() {
        if (receivedMessages.isEmpty()) {
            return null;
        }
        return receivedMessages.get(receivedMessages.size() - 1);
    }

    /**
     * Waits for a message to be received.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit
     * @return true if a message was received within the timeout
     * @throws InterruptedException if the thread is interrupted
     */
    public boolean waitForMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    /**
     * Resets the latch for waiting for the next message.
     */
    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    /**
     * Resets the latch with expected message count.
     *
     * @param count expected number of messages
     */
    public void resetLatch(int count) {
        latch = new CountDownLatch(count);
    }

    /**
     * Clears all received messages.
     */
    public void clearMessages() {
        receivedMessages.clear();
        resetLatch();
    }

    /**
     * Gets the count of received messages.
     *
     * @return count of received messages
     */
    public int getMessageCount() {
        return receivedMessages.size();
    }
}
