package com.dmadev.catalogue.repository;

import com.dmadev.catalogue.BaseIntegrationRepositoryTest;
import com.dmadev.catalogue.entity.Product;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Sql("/sql/products.sql")
class ProductRepositoryIT extends BaseIntegrationRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("findAllByTitle must return filter result")
    void findAllByTitleLikeIgnoreCase_ReturnsFilteredProductsList() {
        //given
        var filter="%macbook%";
        //when
        var products=this.productRepository.findAllByTitleLikeIgnoreCase(filter);
        //then
        assertEquals(List.of(new Product(2,"MacBook","Great PC")),products);
    }
}