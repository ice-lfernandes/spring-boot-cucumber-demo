package com.example.demo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer Service for publishing messages to Kafka topics.
 */
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a message to the specified topic.
     *
     * @param topic   the Kafka topic
     * @param key     the message key
     * @param message the message content
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, String>> publishMessage(String topic, String key, String message) {
        return kafkaTemplate.send(topic, key, message);
    }

    /**
     * Publishes a message to the specified topic without a key.
     *
     * @param topic   the Kafka topic
     * @param message the message content
     * @return CompletableFuture with the send result
     */
    public CompletableFuture<SendResult<String, String>> publishMessage(String topic, String message) {
        return kafkaTemplate.send(topic, message);
    }
}
