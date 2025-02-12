package com.k10.misc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Getter
public class NotificationService {
    private EmailClient urgentMailer;
    private final EmailClient regularMailer;

    private Integer count = 0;

    public NotificationService(
            @Qualifier("regularMailer") EmailClient regularMailer
    ) {
        this.regularMailer = regularMailer;
    }

    @Autowired(required = false)
    public void setUrgentMailer(@Qualifier("urgentMailer") EmailClient urgentMailer) {
        this.urgentMailer = urgentMailer;
    }

    public void sendPasswordReset(String email) {
        this.count++;
    }

    public void sendNewsletter(String email) {
        //regularMailer.send(email, "Monthly Newsletter", "...");
    }
}
