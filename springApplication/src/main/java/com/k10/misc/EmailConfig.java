package com.k10.misc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {
//    @Bean
//    @Qualifier("urgentMailer")
//    public EmailClient urgentMailer() {
//        return EmailClient.builder()
//                .build();
//    }

    @Bean
    @Qualifier("regularMailer")
    public EmailClient regularMailer() {
        return EmailClient.builder()
                .build();
    }
}
