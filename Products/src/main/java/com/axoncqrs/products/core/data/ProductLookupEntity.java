package com.axoncqrs.products.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "product_lookup")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductLookupEntity implements Serializable {

    @Id
    private String id;

    @Column(unique = true)
    private String title;

}
