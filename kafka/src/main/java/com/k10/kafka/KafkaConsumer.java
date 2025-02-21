package com.k10.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class KafkaConsumer {

    private final Set<String> processedOffsets = ConcurrentHashMap.newKeySet();

    @KafkaListener(topics = "topic1", groupId = "consumerGroup1", concurrency = "2")
    public void consume(ConsumerRecord<String, Message> record) {
        long offset = record.offset();
        int partition = record.partition();
        String threadName = Thread.currentThread().getName();
        String key = partition + "-" + offset; // Unique identifier for each message


        // Ensure each offset is processed only once per partition
        if (!processedOffsets.add(key)) { // If already exists, it's a duplicate
            throw new IllegalStateException("Duplicate message detected! Partition: " + partition + ", Offset: " + offset);
        }

        System.out.println("Received by " + threadName +
                " | Partition: " + partition +
                " | Offset: " + offset +
                " | Message: " + record.value());
    }
}

