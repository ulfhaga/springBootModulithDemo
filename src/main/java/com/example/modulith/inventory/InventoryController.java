package com.example.modulith.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody InventoryRequest request) {
        Inventory inventory = inventoryService.createOrUpdateInventory(
            request.productName(),
            request.quantity()
        );
        return ResponseEntity.ok(InventoryResponse.from(inventory));
    }

    @GetMapping("/{productName}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable String productName) {
        Inventory inventory = inventoryService.getInventory(productName);
        return ResponseEntity.ok(InventoryResponse.from(inventory));
    }

    public record InventoryRequest(
        String productName,
        Integer quantity
    ) {}

    public record InventoryResponse(
        Long id,
        String productName,
        Integer availableQuantity,
        Integer reservedQuantity
    ) {
        static InventoryResponse from(Inventory inventory) {
            return new InventoryResponse(
                inventory.getId(),
                inventory.getProductName(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
            );
        }
    }
}
