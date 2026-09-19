package com.example.demo.integration;

import com.example.demo.ClaimsServiceApplication;
import com.example.demo.adapter.out.messaging.KafkaDomainEventPublisher;
import com.example.demo.domain.claim.events.ClaimSubmitted;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ClaimsServiceApplication.class)
@EmbeddedKafka(
        partitions = 1,
        topics = "claim-events"
)
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "app.events.cdc-enabled=false",
        "kafka.producer.schema-validation.enabled=false"
})
@Tag("integration")
class ClaimKafkaIntegrationTest {

    @Autowired
    private KafkaDomainEventPublisher publisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void shouldPublishClaimSubmittedEventToKafka() {

        UUID claimId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ClaimSubmitted event = new ClaimSubmitted(
                UUID.randomUUID().toString(),
                claimId,
                userId,
                LocalDate.of(2026, 9, 18),
                new BigDecimal("5000.00"),
                Instant.now()
        );

        Consumer<String, String> consumer = createConsumer();

        consumer.subscribe(Collections.singletonList("claim-events"));

        publisher.publish(event);

        ConsumerRecord<String, String> record = consumer
                .poll(Duration.ofSeconds(10))
                .iterator()
                .next();

        assertEquals(claimId.toString(), record.key());

        String message = record.value();

        assertTrue(message.contains("\"eventType\":\"claim-submitted\""));
        assertTrue(message.contains(claimId.toString()));
        assertTrue(message.contains(userId.toString()));

        consumer.close();
    }

    private Consumer<String, String> createConsumer() {

        HashMap<String, Object> properties = new HashMap<>();

        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                embeddedKafka.getBrokersAsString()
        );

        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "claim-kafka-integration-test-" + UUID.randomUUID()
        );

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        var factory =
                new org.springframework.kafka.core.DefaultKafkaConsumerFactory<String, String>(
                        properties
                );

        return factory.createConsumer();
    }
}