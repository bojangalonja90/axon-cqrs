package com.axoncqrs.products.command.rest;

import com.axoncqrs.products.command.CreateProductCommand;
import com.axoncqrs.products.command.ProductAggregate;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {

    Logger LOG = LoggerFactory.getLogger(ProductsCommandController.class);

    private final CommandGateway commandGateway;

    @Autowired
    public ProductsCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }


    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody CreateProductModelDTO createProductModelDTO) {

        LOG.info("*** ProductsCommandController createProduct");

        CreateProductCommand createProductCommand = CreateProductCommand.builder().id(UUID.randomUUID().toString())
                .price(createProductModelDTO.getPrice())
                .quantity(createProductModelDTO.getQuantity())
                .title(createProductModelDTO.getTitle()).build();

        String returnValue = commandGateway.sendAndWait(createProductCommand);

        return ResponseEntity.ok().body(returnValue);
    }

}
