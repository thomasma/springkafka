package com.aver.springkafka;

import com.aver.model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {
    "logging.level.org.apache.kafka=WARN",
    "logging.level.org.springframework.kafka=WARN"
})
class MessageConsumerIntegrationTest extends BaseKafkaIntegrationTest {

    @MockitoSpyBean
    private MessageConsumer messageConsumer;

    @Test
    void shouldConsumeMessageFromKafkaTopic() throws Exception {
        Message testMessage = new Message("Test message for consumer integration");

        try (KafkaProducer<String, Message> producer = createTestProducer()) {
            ProducerRecord<String, Message> record = new ProducerRecord<>(MessageProducer.TOPIC, testMessage);
            producer.send(record).get();
        }

        TimeUnit.SECONDS.sleep(3);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageConsumer, atLeastOnce()).consumeMessages(captor.capture());

        List<Message> consumed = captor.getAllValues();
        assertTrue(consumed.stream().anyMatch(m -> "Test message for consumer integration".equals(m.getText())),
                "Consumer should have received the test message");
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

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageConsumer, atLeast(3)).consumeMessages(captor.capture());

        List<String> texts = captor.getAllValues().stream().map(Message::getText).toList();
        assertTrue(texts.contains("First test message"), "Should consume first message");
        assertTrue(texts.contains("Second test message"), "Should consume second message");
        assertTrue(texts.contains("Third test message"), "Should consume third message");
    }

    @Test
    void shouldConsumeMessageWithSpecificContent() throws Exception {
        Message specificMessage = new Message("Specific content for verification");

        try (KafkaProducer<String, Message> producer = createTestProducer()) {
            ProducerRecord<String, Message> record = new ProducerRecord<>(MessageProducer.TOPIC, specificMessage);
            producer.send(record).get();
        }

        TimeUnit.SECONDS.sleep(3);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageConsumer, atLeastOnce()).consumeMessages(captor.capture());

        Message received = captor.getAllValues().stream()
                .filter(m -> "Specific content for verification".equals(m.getText()))
                .findFirst()
                .orElse(null);
        assertNotNull(received, "Consumer should have received the specific message");
        assertEquals("Specific content for verification", received.getText());
    }

    private KafkaProducer<String, Message> createTestProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new KafkaProducer<>(props);
    }
}
