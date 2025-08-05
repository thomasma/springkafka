package com.aver.springkafka;

import com.aver.model.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {
    "logging.level.org.apache.kafka=WARN",
    "logging.level.org.springframework.kafka=WARN"
})
class MessageProducerIntegrationTest extends BaseKafkaIntegrationTest {

    @Autowired
    private MessageProducer messageProducer;

    @Test
    void shouldSendMessageToKafkaTopic() throws InterruptedException {
        Message testMessage = new Message("Test message for integration");
        
        messageProducer.sendMessage(testMessage);
        
        TimeUnit.SECONDS.sleep(2);
        
        try (KafkaConsumer<String, Message> consumer = createTestConsumer()) {
            consumer.subscribe(Collections.singletonList(MessageProducer.TOPIC));
            
            Iterable<ConsumerRecord<String, Message>> recordsIterable = consumer.poll(Duration.ofSeconds(10))
                    .records(MessageProducer.TOPIC);
            List<ConsumerRecord<String, Message>> records = new java.util.ArrayList<>();
            recordsIterable.forEach(records::add);
            
            assertFalse(records.isEmpty(), "Should receive at least one message");
            
            ConsumerRecord<String, Message> record = records.get(0);
            assertNotNull(record.value());
            assertEquals("Test message for integration", record.value().getText());
        }
    }

    @Test
    void shouldSendMultipleMessages() throws InterruptedException {
        Message message1 = new Message("First message");
        Message message2 = new Message("Second message");
        
        messageProducer.sendMessage(message1);
        messageProducer.sendMessage(message2);
        
        TimeUnit.SECONDS.sleep(2);
        
        try (KafkaConsumer<String, Message> consumer = createTestConsumer()) {
            consumer.subscribe(Collections.singletonList(MessageProducer.TOPIC));
            
            Iterable<ConsumerRecord<String, Message>> recordsIterable = consumer.poll(Duration.ofSeconds(10))
                    .records(MessageProducer.TOPIC);
            List<ConsumerRecord<String, Message>> records = new java.util.ArrayList<>();
            recordsIterable.forEach(records::add);
            
            assertTrue(records.size() >= 2, "Should receive at least two messages");
            
            boolean foundFirst = records.stream()
                    .anyMatch(record -> "First message".equals(record.value().getText()));
            boolean foundSecond = records.stream()
                    .anyMatch(record -> "Second message".equals(record.value().getText()));
            
            assertTrue(foundFirst, "Should find first message");
            assertTrue(foundSecond, "Should find second message");
        }
    }

    private KafkaConsumer<String, Message> createTestConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.aver.model");
        
        return new KafkaConsumer<>(props);
    }
}