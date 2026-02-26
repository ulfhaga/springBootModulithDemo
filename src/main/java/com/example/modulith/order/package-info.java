/**
 * Order Module
 * 
 * This module handles order management and publishes domain events.
 * 
 * Public API:
 * - OrderPlacedEvent: Published when an order is created
 * - OrderService: For external modules to interact with orders (if needed)
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Order Management",
    allowedDependencies = {}
)
package com.example.modulith.order;
