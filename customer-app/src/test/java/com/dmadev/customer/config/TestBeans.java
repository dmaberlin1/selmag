package com.dmadev.customer.config;

import com.dmadev.customer.client.WebClientFavoritesProductClient;
import com.dmadev.customer.client.WebClientProductReviewsClient;
import com.dmadev.customer.client.WebClientProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

@Configuration
public class TestBeans {

    @Bean
   public ReactiveClientRegistrationRepository clientRegistrationRepository(){
        return mock();
    };

    @Bean
   public ServerOAuth2AuthorizedClientRepository authorizedClientRepository(){
        return mock();
    };

    final String baseUrl = "http://localhost:54321";


    @Bean
    @Primary
    public WebClientProductsClient mockWebClientProductsClient() {
        return new WebClientProductsClient(
                WebClient.builder()
                        .baseUrl(baseUrl)
                        .build());
    }


    @Bean
    @Primary
    public WebClientFavoritesProductClient mockWebClientFavoritesProductsClient() {
        return new WebClientFavoritesProductClient(WebClient.builder()
                .baseUrl(baseUrl)
                .build());
    }


    @Bean
    @Primary
    public WebClientProductReviewsClient mockWebClientProductReviewsClient() {
        return new WebClientProductReviewsClient(WebClient.builder()
                .baseUrl(baseUrl)
                .build());
    }

}
