package com.dmadev.feedback.controller;

import com.dmadev.feedback.controller.payload.NewProductReviewPayload;
import com.dmadev.feedback.entity.ProductReview;
import com.dmadev.feedback.service.ProductReviewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsRestController {

    private final ProductReviewsService productReviewsService;

    //?productId={productId}
    //by-product-id/{productId}
    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> findProductsReviewsByProductId(@PathVariable("productId") int productId) {
        return this.productReviewsService.findProductReviewsByProductId(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return payloadMono
                .flatMap(payload -> this.productReviewsService
                        .createProductReview(payload.productId(), payload.rating(), payload.review()))
                .map(productReview -> ResponseEntity
                        .created(uriComponentsBuilder
                                .replacePath("/feedback-api/product-reviews/{id}")
                                .build(Map.of("id",productReview.getId())))
//                                .build(productReview.getId())
                        .body(productReview));
    }



    //eof
}
