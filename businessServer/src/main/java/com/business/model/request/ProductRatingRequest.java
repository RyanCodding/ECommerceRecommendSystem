package com.business.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRatingRequest {

    private int userId;

    private int productId;

    private Double score;

}
