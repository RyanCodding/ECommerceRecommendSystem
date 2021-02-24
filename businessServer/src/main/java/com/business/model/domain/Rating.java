package com.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
public class Rating {

    @JsonIgnore
    private String _id;

    private int userId;

    private int productId;

    private double score;

    private long timestamp;

    public Rating(int userId, int productId, double score) {
        this.userId = userId;
        this.productId = productId;
        this.score = score;
        this.timestamp = new Date().getTime();
    }

}