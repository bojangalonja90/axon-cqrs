package com.axoncqrs.products.query;

import com.axoncqrs.products.core.data.ProductRepository;
import com.axoncqrs.products.query.rest.ProductDTO;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ProcessingGroup("product-group")
public class ProductsQueryHandler {

    Logger LOG = LoggerFactory.getLogger(ProductEventHandler.class);

    private final ProductRepository productRepository;

    public ProductsQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductDTO> findProducts(FindProductsQuery query) {

        LOG.info("*** QueryHandler ProductsQueryHandler");

        List<ProductDTO> productDTOs = productRepository
                .findAll().stream()
                .map(entity -> {
                    ProductDTO productDTO = new ProductDTO();
                    BeanUtils.copyProperties(entity, productDTO);
                    return productDTO;
                })
                .collect(Collectors.toList());

        return productDTOs;
    }
}
