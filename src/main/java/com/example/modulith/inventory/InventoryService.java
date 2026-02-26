package com.example.modulith.inventory;

import com.example.modulith.order.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Event listener that reacts to OrderPlacedEvent.
     * This demonstrates how modules can communicate via events in Spring Modulith.
     * 
     * The @ApplicationModuleListener annotation ensures this listener is invoked
     * asynchronously and transactionally.
     */
    @ApplicationModuleListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order ID: {}, product: {}", 
            event.orderId(), event.productName());
        
        try {
            // Find or create inventory for the product
            Inventory inventory = inventoryRepository
                .findByProductName(event.productName())
                .orElseThrow(() -> new RuntimeException(
                    "No inventory found for product: " + event.productName()));
            
            // Reserve the inventory
            inventory.reserve(event.quantity());
            inventoryRepository.save(inventory);
            
            log.info("Reserved {} units of {} for order ID: {}", 
                event.quantity(), event.productName(), event.orderId());
            
        } catch (Exception e) {
            log.error("Failed to reserve inventory for order ID: {}", event.orderId(), e);
            throw e; // This will cause the event to be retried if configured
        }
    }

    @Transactional
    public Inventory createOrUpdateInventory(String productName, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductName(productName)
            .orElse(new Inventory(productName, quantity));
        
        if (inventory.getId() != null) {
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        }
        
        return inventoryRepository.save(inventory);
    }

    public Inventory getInventory(String productName) {
        return inventoryRepository.findByProductName(productName)
            .orElseThrow(() -> new RuntimeException("Inventory not found for: " + productName));
    }
}
