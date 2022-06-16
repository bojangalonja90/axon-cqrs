package com.axoncqrs.orders.orders.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<OrderEntity, String> {

    public Optional<OrderEntity> findByOrderId(String orderId);

}
