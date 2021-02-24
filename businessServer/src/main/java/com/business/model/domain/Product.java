package com.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Product {

    @JsonIgnore
    private String _id;

    private int productId;

    private String name;

    private String imageUrl;

    private Double score;

    private String categories;

    private String tags;


}