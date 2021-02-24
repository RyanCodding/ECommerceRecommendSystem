package com.business.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRecommendationRequest {
    private int userId;

    private int sum;

}
