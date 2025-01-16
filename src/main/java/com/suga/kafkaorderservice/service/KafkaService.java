package com.suga.kafkaorderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suga.kafkaorderservice.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaService {

    @Autowired
    private NewTopic topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendProductNotification(Product product) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String messageWrite = mapper.writeValueAsString(product);
        Message<String> message = MessageBuilder
                .withPayload(messageWrite)
                .setHeader(KafkaHeaders.TOPIC, topic.name())
                .build();
        kafkaTemplate.send(message);
        log.debug("message send");
    }

}
