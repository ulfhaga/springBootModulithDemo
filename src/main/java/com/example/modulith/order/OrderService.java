package com.example.modulith.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates a new order and publishes an OrderPlacedEvent.
     * This demonstrates event publishing in Spring Modulith.
     */
    @Transactional
    public Order placeOrder(String customerName, String productName, Integer quantity, BigDecimal totalAmount) {
        log.info("Placing order for customer: {}, product: {}", customerName, productName);
        
        // Create and save the order
        Order order = new Order(customerName, productName, quantity, totalAmount);
        order = orderRepository.save(order);
        
        // Publish the domain event
        OrderPlacedEvent event = OrderPlacedEvent.from(order);
        eventPublisher.publishEvent(event);
        
        log.info("Order placed with ID: {} and event published", order.getId());
        
        return order;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
}
