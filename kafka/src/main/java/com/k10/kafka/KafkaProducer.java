package com.k10.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, Message> kafkaTemplate;
    public KafkaProducer(@Qualifier("objectTemplate") KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic,Message message) {
        kafkaTemplate.send(topic, message);
        System.out.println("Sent message: " + message);
    }
}
