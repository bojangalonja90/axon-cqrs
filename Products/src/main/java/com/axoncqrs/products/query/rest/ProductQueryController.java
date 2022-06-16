package com.axoncqrs.products.query.rest;

import com.axoncqrs.products.query.FindProductsQuery;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    @Autowired
    QueryGateway queryGateway;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts() {

        FindProductsQuery findProductsQuery = new FindProductsQuery();
        List<ProductDTO> res = queryGateway.query(findProductsQuery,
                ResponseTypes.multipleInstancesOf(ProductDTO.class)).join();

        return ResponseEntity.ok(res);
    }

}
