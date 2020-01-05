package com.aver.springkafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aver.model.Message;

@RestController
public class MessagePublisherController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePublisherController.class);

    @Autowired
    private MessageProducer msgProducer;

    @PostMapping("/publish")
    public Message echo(@RequestBody Message message) {
        LOGGER.info("received message from rest endpoint {}", message);
        msgProducer.sendMessage(message);
        return message;
    }

    @GetMapping("/test")
    public Message echo() {
        return new Message("test message");
    }
}