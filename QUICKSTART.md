# Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Step 1: Build the Project

```bash
cd spring-modulith-demo
mvn clean install
```

### Step 2: Run the Application

```bash
mvn spring-boot:run
```

Wait for the application to start. You should see:
```
Started ModulithApplication in X seconds
```

### Step 3: Run the Test Script

Open a new terminal and run:

```bash
./test-commands.sh
```

### Step 4: Watch the Magic! ✨

Observe the application logs to see the event flow:

```
OrderService         : Placing order for customer: John Doe, product: Laptop
OrderService         : Order placed with ID: 1 and event published
InventoryService     : Received OrderPlacedEvent for order ID: 1, product: Laptop
InventoryService     : Reserved 2 units of Laptop for order ID: 1
NotificationService  : === NOTIFICATION SERVICE ===
NotificationService  : Sending order confirmation to customer: John Doe
NotificationService  : Notification sent successfully!
```

## 🎯 What Just Happened?

1. **Order Created**: OrderService created an order in the database
2. **Event Published**: `OrderPlacedEvent` was published
3. **Inventory Updated**: InventoryService listened to the event and reserved stock
4. **Notification Sent**: NotificationService listened to the event and sent a notification

**All asynchronously and independently!**

## 🔍 Manual Testing

### Create Inventory

```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"productName": "Laptop", "quantity": 100}'
```

### Place an Order (This publishes the event!)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "productName": "Laptop",
    "quantity": 2,
    "totalAmount": 2000.00
  }'
```

### Check Inventory (see reserved stock)

```bash
curl http://localhost:8080/api/inventory/Laptop
```

Expected response:
```json
{
  "id": 1,
  "productName": "Laptop",
  "availableQuantity": 98,
  "reservedQuantity": 2
}
```

## 🧪 Run Tests

```bash
mvn test
```

This will:
- Verify module boundaries
- Generate architecture documentation in `target/modulith-docs/`
- Test event publishing and consumption

## 📊 View Generated Documentation

After running tests, check:

```bash
ls -la target/modulith-docs/
```

You'll find:
- `modulith-structure.adoc` - Module structure documentation
- `*.puml` - PlantUML diagrams of your architecture

## 🎓 Next Steps

1. **Modify the code**: Try adding a new event listener
2. **Add a module**: Create a Shipping module that also listens to OrderPlacedEvent
3. **Break boundaries**: Try to access internal classes from another module (the tests will fail!)
4. **Add event persistence**: Configure event storage for replay capabilities

## 💡 Tips

- Watch the logs to see event flow
- H2 Console is available at: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:modulithdb`
  - Username: `sa`
  - Password: (empty)

Enjoy exploring Spring Modulith! 🎉
