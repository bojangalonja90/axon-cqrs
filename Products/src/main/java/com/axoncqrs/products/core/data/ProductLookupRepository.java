package com.axoncqrs.products.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLookupRepository extends JpaRepository<ProductLookupEntity, String> {

    ProductLookupEntity findByIdOrTitle(String id, String title);

}
