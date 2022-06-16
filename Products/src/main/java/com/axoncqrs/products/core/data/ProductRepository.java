package com.axoncqrs.products.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    ProductEntity findByIdOrTitle(String id, String title);
}
