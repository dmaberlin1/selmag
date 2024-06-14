package com.dmadev.customer.config;

import com.dmadev.customer.client.WebClientFavoritesProductClient;
import com.dmadev.customer.client.WebClientProductReviewsClient;
import com.dmadev.customer.client.WebClientProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {
    @Bean
    public WebClientFavoritesProductClient webClientFavoritesProductsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String feedbackBaseUrl
    ){
      return new WebClientFavoritesProductClient(WebClient.builder()
              .baseUrl(feedbackBaseUrl)
              .build());
    };
    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8083}") String feedbackBaseUrl
    ){
      return new WebClientProductReviewsClient(WebClient.builder()
              .baseUrl(feedbackBaseUrl)
              .build());
    };
    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl
    ){
      return new WebClientProductsClient(WebClient.builder()
              .baseUrl(catalogueBaseUrl)
              .build());
    };
}
