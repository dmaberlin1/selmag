package com.dmadev.catalogue.repository;

import com.dmadev.catalogue.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product,Integer> {
    Iterable<Product> findAllByTitleLikeIgnoreCase(String filter);
}
