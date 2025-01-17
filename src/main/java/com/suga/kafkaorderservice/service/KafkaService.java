package com.suga.kafkaorderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suga.kafkaorderservice.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {

    private final NewTopic topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendProductNotification(Product product) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String messageWrite = mapper.writeValueAsString(product);
            Message<String> message = MessageBuilder
                    .withPayload(messageWrite)
                    .setHeader(KafkaHeaders.TOPIC, topic.name())
                    .build();

            kafkaTemplate.send(message);
            log.debug("Successfully sent message to Kafka topic: {}", topic.name());
        } catch (Exception exception) {
            log.error("Error occurred while sending product notification to Kafka: {}", exception.getMessage(), exception);
            throw new RuntimeException("Failed to send product notification", exception);
        }
    }


}
