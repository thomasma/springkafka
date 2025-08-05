package com.aver.springkafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

import com.aver.model.Message;

/**
 * Kafka hello world message producer
 */
@Component
public class MessageProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProducer.class);

    static final String TOPIC = "testtopic";

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    public void sendMessage(Message message) {
        CompletableFuture<SendResult<String, Message>> future = kafkaTemplate.send(TOPIC, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("Sent message with offset=[{}]", result.getRecordMetadata().offset());
            } else {
                LOGGER.error("Unable to send message due to {}", ex.getMessage());
            }
        });
    }
}
