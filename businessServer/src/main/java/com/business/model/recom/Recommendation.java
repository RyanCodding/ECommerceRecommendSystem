package com.business.model.recom;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 推荐项目的包装
 */
@Data
@AllArgsConstructor
public class Recommendation {

    private int productId;

    private Double score;


}
