package com.dmadev.feedback.service;


import com.dmadev.feedback.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(int productId, int rating, String review,String userId);

    Flux<ProductReview> findProductReviewsByProductId(int productId);
}
