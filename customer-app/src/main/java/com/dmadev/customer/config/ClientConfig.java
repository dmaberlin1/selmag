package com.dmadev.customer.config;

import com.dmadev.customer.client.WebClientFavoritesProductClient;
import com.dmadev.customer.client.WebClientProductReviewsClient;
import com.dmadev.customer.client.WebClientProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient.Builder selmagServicesWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository clientRepository) {

        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                clientRegistrationRepository, clientRepository
        );
        filter.setDefaultClientRegistrationId("keycloack");
        return WebClient.builder()
                .filter(filter);
    }

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${selmag.services.catalogue.uri:http://localhost:8081}") String catalogueBaseUrl,
            WebClient.Builder selmagServicesWebClientBuilder
    ) {
        return new WebClientProductsClient(selmagServicesWebClientBuilder
                .baseUrl(catalogueBaseUrl)
                .build());
    }


    @Bean
    public WebClientFavoritesProductClient webClientFavoritesProductsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder selmagServicesWebClientBuilder
    ) {
        return new WebClientFavoritesProductClient(selmagServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }


    @Bean
    public WebClientProductReviewsClient webClientProductReviewsClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8084}") String feedbackBaseUrl,
            WebClient.Builder selmagServicesWebClientBuilder
    ) {
        return new WebClientProductReviewsClient(selmagServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }


    //eof
}
