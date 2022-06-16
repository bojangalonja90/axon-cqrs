package com.axoncqrs.orders.orders.command.rest;

import com.axoncqrs.orders.orders.command.CreateOrderCommand;
import com.axoncqrs.orders.orders.command.OrderStatus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final CommandGateway commandGateway;

    public OrdersCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderCommandDTO order) {

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(UUID.randomUUID().toString())
                .orderStatus(OrderStatus.CREATED)
                .addressId(order.getAddressId())
                .quantity(order.getQuantity())
                .productId(order.getProductId())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .build();

        commandGateway.sendAndWait(createOrderCommand);

        return ResponseEntity.ok().body("Order created: " + createOrderCommand.getOrderId());
    }

}
