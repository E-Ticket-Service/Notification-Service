package abb.tech.notification_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequestEvent {
    private String paymentIntentId;
    private BigDecimal amount;
    private Long orderId;
}
