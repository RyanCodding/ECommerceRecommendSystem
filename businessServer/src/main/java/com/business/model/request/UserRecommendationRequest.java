package com.business.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRecommendationRequest {

    private int userId;

    private String username;

    private int sum;

    public UserRecommendationRequest(int userId, int sum) {
        this.userId = userId;
        this.sum = sum;
    }
}
