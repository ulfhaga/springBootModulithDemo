#!/bin/bash

echo "================================"
echo "Spring Modulith Event Demo Tests"
echo "================================"
echo ""

set -e

# Wait for application to start
echo "Waiting for application to start..."
sleep 2

echo "1. Creating Inventory for Laptop..."
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "quantity": 100
  }'
echo -e "\n"

echo "2. Creating Inventory for Phone..."
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Phone",
    "quantity": 50
  }'
echo -e "\n"

echo "3. Placing Order for Laptop (This publishes OrderPlacedEvent)..."
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "productName": "Laptop",
    "quantity": 2,
    "totalAmount": 2000.00
  }'
echo -e "\n"

echo "4. Placing Order for Phone..."
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Jane Smith",
    "productName": "Phone",
    "quantity": 3,
    "totalAmount": 900.00
  }'
echo -e "\n"

# Give time for async events to process
echo "Waiting for async event processing..."
sleep 1

echo "5. Checking Laptop Inventory (should show reserved quantity)..."
curl http://localhost:8080/api/inventory/Laptop
echo -e "\n"

echo "6. Checking Phone Inventory..."
curl http://localhost:8080/api/inventory/Phone
echo -e "\n"

echo "7. Getting Order Details..."
curl http://localhost:8080/api/orders/1
echo -e "\n"

echo ""
echo "================================"
echo "Check the application logs to see:"
echo "  - OrderService publishing events"
echo "  - InventoryService reserving stock"
echo "  - NotificationService sending notifications"
echo "================================"
