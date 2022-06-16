package com.axoncqrs.products.query;

import com.axoncqrs.corelib.events.ProductReservationCancelledEvent;
import com.axoncqrs.corelib.events.ProductReservedEvent;
import com.axoncqrs.products.command.ProductAggregate;
import com.axoncqrs.products.core.data.ProductEntity;
import com.axoncqrs.products.core.data.ProductRepository;
import com.axoncqrs.products.core.events.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    Logger LOG = LoggerFactory.getLogger(ProductEventHandler.class);

    private final ProductRepository productRepository;

    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) throws Exception {
        //LOG
        throw e;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) throws Exception {

        LOG.info("*** ProductEventHandler");

        if(productRepository.existsById(productCreatedEvent.getId())) {
            throw new IllegalArgumentException("Product exists " + productCreatedEvent.getId());
        }

        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, productEntity);

        productRepository.save(productEntity);
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) throws Exception {

        LOG.info("*** ProductReservedEvent");

        Optional<ProductEntity> productEntity = productRepository.findById(productReservedEvent.getProductId());

        if(productEntity.isEmpty()) {
            throw new IllegalArgumentException("No product exists " + productReservedEvent.getProductId());
        }
        ProductEntity product = productEntity.get();
        product.setQuantity(product.getQuantity() - productReservedEvent.getQuantity());

        productRepository.save(product);
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent reservationCancelledEvent) {

        LOG.info("Persisiting new quantity for cancelled ProductReservationCancelledEvent event");

        Optional<ProductEntity> productEntity = productRepository.findById(reservationCancelledEvent.getProductId());

        if(productEntity.isEmpty()) {
            throw new IllegalArgumentException("No product exists " + reservationCancelledEvent.getProductId());
        }
        ProductEntity product = productEntity.get();
        product.setQuantity(product.getQuantity() + reservationCancelledEvent.getQuantity());

        productRepository.save(product);

    }

}
