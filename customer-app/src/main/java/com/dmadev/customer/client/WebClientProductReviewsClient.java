package com.dmadev.customer.client;

import com.dmadev.customer.client.exception.ClientBadRequestException;
import com.dmadev.customer.client.payload.NewProductReviewPayload;
import com.dmadev.customer.entity.ProductReview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class WebClientProductReviewsClient implements ProductReviewsClient {



    private final WebClient webClient;

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return this.webClient.get()
                .uri("/feedback-api/product-reviews/by-product-id/{productId}",productId).
                retrieve()
                .bodyToFlux(ProductReview.class);
    }

    @Override
    public Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review) {
        return this.webClient
                .post()
                .uri("/feedback-api/product-reviews")
                .bodyValue(new NewProductReviewPayload(productId,rating,review))
                .retrieve()
                .bodyToMono(ProductReview.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception ->new ClientBadRequestException(
                                exception,
                                ((List<String>)exception.getResponseBodyAs(ProblemDetail.class)
                                        .getProperties().get("errors")))
                );
    }


    //eof
}
