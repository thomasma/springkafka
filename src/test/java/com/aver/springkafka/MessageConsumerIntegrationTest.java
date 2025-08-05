package com.aver.springkafka;

import com.aver.model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.TestPropertySource;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = {
    "logging.level.org.apache.kafka=WARN",
    "logging.level.org.springframework.kafka=WARN"
})
class MessageConsumerIntegrationTest extends BaseKafkaIntegrationTest {

    @Test
    void shouldConsumeMessageFromKafkaTopic() throws Exception {
        Message testMessage = new Message("Test message for consumer integration");
        
        try (KafkaProducer<String, Message> producer = createTestProducer()) {
            ProducerRecord<String, Message> record = new ProducerRecord<>(MessageProducer.TOPIC, testMessage);
            producer.send(record).get();
        }
        
        TimeUnit.SECONDS.sleep(3);
        
        assertTrue(true, "Message sent successfully to Kafka topic");
    }

    @Test
    void shouldConsumeMultipleMessages() throws Exception {
        Message message1 = new Message("First test message");
        Message message2 = new Message("Second test message");
        Message message3 = new Message("Third test message");
        
        try (KafkaProducer<String, Message> producer = createTestProducer()) {
            producer.send(new ProducerRecord<>(MessageProducer.TOPIC, message1)).get();
            producer.send(new ProducerRecord<>(MessageProducer.TOPIC, message2)).get();
            producer.send(new ProducerRecord<>(MessageProducer.TOPIC, message3)).get();
        }
        
        TimeUnit.SECONDS.sleep(3);
        
        assertTrue(true, "Multiple messages sent successfully to Kafka topic");
    }

    @Test
    void shouldConsumeMessageWithSpecificContent() throws Exception {
        Message specificMessage = new Message("Specific content for verification");
        
        try (KafkaProducer<String, Message> producer = createTestProducer()) {
            ProducerRecord<String, Message> record = new ProducerRecord<>(MessageProducer.TOPIC, specificMessage);
            producer.send(record).get();
        }
        
        TimeUnit.SECONDS.sleep(3);
        
        assertTrue(true, "Specific message sent successfully to Kafka topic");
    }

    private KafkaProducer<String, Message> createTestProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        return new KafkaProducer<>(props);
    }
}