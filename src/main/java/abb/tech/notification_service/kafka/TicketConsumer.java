package abb.tech.notification_service.kafka;

import abb.tech.notification_service.dto.RefundRequestEvent;
import abb.tech.notification_service.dto.TicketCreatedEvent;
import abb.tech.notification_service.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TicketConsumer {

    private final EmailService emailService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String REFUND_REQUEST_TOPIC = "refund-request-topic";


    @KafkaListener(topics = "ticket-created-topic", groupId = "notification-group")
    public void consumeTicketCreatedEvent(TicketCreatedEvent event) {
        log.info("Kafka-dan yeni bilet eventi alındı: Order ID {}", event.getOrderId());
        try {
            emailService.sendTicketWithAttachment(event);
            log.info("E-mail uğurla göndərildi: {}", event.getUserEmail());
        } catch (Exception e) {
            log.error("E-mail göndərərkən xəta baş verdi: {}", e.getMessage());
            sendRefundRequest(event);
        }
    }

    private void sendRefundRequest(TicketCreatedEvent event) {
        if (event.getPaymentIntentId() == null) {
            log.error("Refund göndərilə bilməz: Payment intent ID boşdur. Order ID: {}", event.getOrderId());
            return;
        }

        RefundRequestEvent refundEvent = RefundRequestEvent.builder()
                .paymentIntentId(event.getPaymentIntentId())
                .amount(event.getTotalAmount())
                .orderId(event.getOrderId())
                .build();

        try {
            String jsonEvent = objectMapper.writeValueAsString(refundEvent);
            kafkaTemplate.send(REFUND_REQUEST_TOPIC, jsonEvent);
            log.info("Refund eventi Kafka-ya göndərildi: Order ID {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Refund eventi göndərilərkən xəta: {}", e.getMessage());
        }
    }
}