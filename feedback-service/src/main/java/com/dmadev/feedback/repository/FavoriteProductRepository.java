package com.dmadev.feedback.repository;

import com.dmadev.feedback.entity.FavoriteProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavoriteProductRepository
        extends ReactiveCrudRepository<FavoriteProduct, UUID> {

    Flux<FavoriteProduct> findAllByUserId(String userId);

    Mono<Void> deleteByProductIdAndUserId(int productId, String userId);

    Mono<FavoriteProduct> findByProductIdAndUserId(int productId, String userId);

}
