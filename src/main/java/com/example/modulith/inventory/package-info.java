/**
 * Inventory Module
 * 
 * This module manages product inventory and listens to order events.
 * It demonstrates how modules can react to events published by other modules.
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Inventory Management",
    allowedDependencies = "order"
)
package com.example.modulith.inventory;
