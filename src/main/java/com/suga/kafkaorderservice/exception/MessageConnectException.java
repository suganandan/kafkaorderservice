package com.suga.kafkaorderservice.exception;

import java.io.Serial;

public class MessageConnectException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MessageConnectException(String msg) {
        super(msg);
    }
}