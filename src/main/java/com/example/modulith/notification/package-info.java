/**
 * Notification Module
 * 
 * This module sends notifications to customers.
 * It listens to order events to send order confirmations.
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Notification Service",
    allowedDependencies = "order"
)
package com.example.modulith.notification;
