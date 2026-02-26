package com.example.modulith.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.placeOrder(
            request.customerName(),
            request.productName(),
            request.quantity(),
            request.totalAmount()
        );
        
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    public record OrderRequest(
        String customerName,
        String productName,
        Integer quantity,
        BigDecimal totalAmount
    ) {}

    public record OrderResponse(
        Long id,
        String customerName,
        String productName,
        Integer quantity,
        BigDecimal totalAmount,
        OrderStatus status
    ) {
        static OrderResponse from(Order order) {
            return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus()
            );
        }
    }
}
