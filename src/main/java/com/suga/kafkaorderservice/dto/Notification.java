package com.suga.kafkaorderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
    private Long id;

    private String eventType;

    private Long customerId;

    private String message;

    private String createdOn;
}