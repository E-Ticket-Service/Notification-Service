package abb.tech.notification_service.service;

import abb.tech.notification_service.dto.TicketCreatedEvent;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendTicketWithAttachment(TicketCreatedEvent request) throws MessagingException;
}
