package abb.tech.notification_service.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketCreatedEvent {
    private Long orderId;
    private Long userId;
    private String userEmail;
    private List<TicketDetails> tickets;
    private String status;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TicketDetails {
        private Long ticketId;
        private String ticketNumber;
        private String pdfBase64;
    }
}