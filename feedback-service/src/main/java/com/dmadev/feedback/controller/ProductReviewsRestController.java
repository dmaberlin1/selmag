package com.dmadev.feedback.controller;

import com.dmadev.feedback.controller.payload.NewProductReviewPayload;
import com.dmadev.feedback.entity.ProductReview;
import com.dmadev.feedback.service.ProductReviewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

import java.security.Principal;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
@Slf4j
public class ProductReviewsRestController {

    private final ProductReviewsService productReviewsService;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    //?productId={productId}
    //by-product-id/{productId}
    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> findProductsReviewsByProductId(
            @PathVariable("productId") int productId,
            Mono<Principal> principalMono) {
        return this.reactiveMongoTemplate
                .find(query(Criteria.where("productId").is(productId)),
                        ProductReview.class);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<NewProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return authenticationTokenMono
                .flatMap(token -> payloadMono
                        .flatMap(payload -> this.productReviewsService
                                .createProductReview(payload.productId(),
                                        payload.rating(), payload.review(),
                                        token.getToken().getSubject())))
                .map(productReview -> ResponseEntity
                        .created(uriComponentsBuilder
                                .replacePath("/feedback-api/product-reviews/{id}")
                                .build(Map.of("id", productReview.getId())))
//                                .build(productReview.getId())
                        .body(productReview));
    }


    //eof
}
