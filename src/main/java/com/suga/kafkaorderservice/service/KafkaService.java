package com.suga.kafkaorderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suga.kafkaorderservice.entity.Product;
import com.suga.kafkaorderservice.exception.MessageConnectException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {

    @Value("${spring.kafka.topic.name}")
    private String requestTopicName;

    @Value("${spring.kafka.response.topic.name}")
    private String responseTopicName;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendProductNotification(Product product) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String messageWrite = mapper.writeValueAsString(product);
            Message<String> message = MessageBuilder
                    .withPayload(messageWrite)
                    .setHeader(KafkaHeaders.TOPIC, requestTopicName)
                    .build();

            kafkaTemplate.send(message);
            log.debug("Successfully sent message to Kafka topic: {}", requestTopicName);
        } catch (Exception exception) {
            log.error("Error occurred while sending product notification to Kafka: {}", exception.getMessage(), exception);
            throw new MessageConnectException("Failed to send product notification"+exception);
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.response.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenProductResponse(String response) {
        log.info("Received response from Consumer on topic {}: {}", responseTopicName, response);
        // Update the transaction status accordingly
    }
}
