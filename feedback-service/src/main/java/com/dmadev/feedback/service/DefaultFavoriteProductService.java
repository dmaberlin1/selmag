package com.dmadev.feedback.service;


import com.dmadev.feedback.entity.FavoriteProduct;
import com.dmadev.feedback.repository.FavoriteProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultFavoriteProductService implements FavoriteProductService {

    private final FavoriteProductRepository favoriteProductRepository;

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(int productId,String userId) {
        return this.favoriteProductRepository.save(new FavoriteProduct(UUID.randomUUID(), productId,userId));
    }

    @Override
    public Mono<Void> removeProductFromFavorites(int productId,String userId) {
        return this.favoriteProductRepository.deleteByProductIdAndUserId(productId,userId);
    }

    @Override
    public Mono<FavoriteProduct> findFavoriteProductByProductId(int productId, String userId) {
        return this.favoriteProductRepository.findByProductIdAndUserId(productId,userId);
    }

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts(String userId) {
        return this.favoriteProductRepository.findAllByUserId(userId);
    }

}
