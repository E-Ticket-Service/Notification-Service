package abb.tech.notification_service.kafka;

import abb.tech.notification_service.dto.TicketCreatedEvent;
import abb.tech.notification_service.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TicketConsumer {

    private final EmailService emailService;

    public TicketConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "ticket-created-topic", groupId = "notification-group")
    public void consumeTicketCreatedEvent(TicketCreatedEvent event) {
        log.info("Kafka-dan yeni bilet eventi alındı: Order ID {}", event.getOrderId());
        try {
            emailService.sendTicketWithAttachment(event);
            log.info("E-mail uğurla göndərildi: {}", event.getUserEmail());
        } catch (MessagingException e) {
            log.error("E-mail göndərərkən xəta baş verdi: {}", e.getMessage());
        }
    }
}