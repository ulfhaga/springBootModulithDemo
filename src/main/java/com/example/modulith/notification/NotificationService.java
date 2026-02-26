package com.example.modulith.notification;

import com.example.modulith.order.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

/**
 * Notification service that sends notifications when orders are placed.
 * This demonstrates multiple listeners for the same event.
 */
@Service
@Slf4j
public class NotificationService {

    /**
     * Asynchronous event listener for OrderPlacedEvent.
     * Multiple modules can listen to the same event independently.
     */
    @ApplicationModuleListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("=== NOTIFICATION SERVICE ===");
        log.info("Sending order confirmation to customer: {}", event.customerName());
        log.info("Order Details:");
        log.info("  - Order ID: {}", event.orderId());
        log.info("  - Product: {}", event.productName());
        log.info("  - Quantity: {}", event.quantity());
        log.info("  - Total Amount: ${}", event.totalAmount());
        log.info("  - Order Date: {}", event.orderDate());
        
        // Simulate sending email/SMS notification
        sendNotification(event);
        
        log.info("Notification sent successfully!");
        log.info("============================");
    }

    private void sendNotification(OrderPlacedEvent event) {
        // In a real application, this would integrate with an email/SMS service
        // For demo purposes, we just log it
        String message = String.format(
            "Dear %s, your order #%d for %s has been confirmed. Total: $%s",
            event.customerName(),
            event.orderId(),
            event.productName(),
            event.totalAmount()
        );
        
        log.info("Notification message: {}", message);
    }
}
