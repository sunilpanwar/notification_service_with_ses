package org.ssp.notification;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.ssp.notification.service.SqsMessageReceiver;

@SpringBootApplication
@EnableJms
@EnableConfigurationProperties
@EnableJpaAuditing
public class NotificationSesApplication  implements CommandLineRunner {


    @Autowired
    private SqsMessageReceiver messageReceiver;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(NotificationSesApplication.class, args);
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        // Send a message with a POJO - the template reuse the message converter
        System.out.println("Sending an email message.");
        //jmsTemplate.convertAndSend("mailbox", "Hi this is test messagge");
    }

    @Override
    public void run(String... args) throws Exception
    {
        messageReceiver.pollMessages();
    }


    @PreDestroy
    public void shutdown() {
        System.out.println("Shutting down the application...");


    }
}