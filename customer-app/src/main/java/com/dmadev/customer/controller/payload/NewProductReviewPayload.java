package com.dmadev.customer.controller.payload;


public record NewProductReviewPayload(
        Integer rating,
        String review) {
};
