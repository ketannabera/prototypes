package com.k10.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final KafkaProducer producer;

    @Autowired
    public Application(KafkaProducer producer) {
        this.producer = producer;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello world!");
        for(int i=1;i<1000;i++){
            producer.sendMessage("topic1", new Message("Message"+i, i));
        }
    }
}