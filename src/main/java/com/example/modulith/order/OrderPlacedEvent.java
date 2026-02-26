package com.example.modulith.order;

import org.springframework.modulith.events.Externalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event published when an order is placed.
 * This event is part of the order module's public API and can be consumed by other modules.
 */
@Externalized("order.placed::#{orderDate}")
public record OrderPlacedEvent(
    Long orderId,
    String customerName,
    String productName,
    Integer quantity,
    BigDecimal totalAmount,
    LocalDateTime orderDate
) {
    public static OrderPlacedEvent from(Order order) {
        return new OrderPlacedEvent(
            order.getId(),
            order.getCustomerName(),
            order.getProductName(),
            order.getQuantity(),
            order.getTotalAmount(),
            order.getOrderDate()
        );
    }
}
