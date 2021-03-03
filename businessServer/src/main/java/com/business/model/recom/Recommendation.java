package com.business.model.recom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐项目的包装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {

    private int productId;

    private Double score;


}
