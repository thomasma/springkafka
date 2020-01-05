package com.aver.springkafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.aver.model.Message;

/**
 * Kafka hello world consumer
 */
@Component
public class MessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    @KafkaListener(topics = MessageProducer.TOPIC, groupId = "testgroup")
    public void consumeMessages(Message message) {
        LOGGER.info("\n>>> Received message {} \n ", message.toString());
    }

}
