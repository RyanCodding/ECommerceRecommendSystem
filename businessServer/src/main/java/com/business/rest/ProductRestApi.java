package com.business.rest;

import com.business.model.domain.Product;
import com.business.model.domain.User;
import com.business.model.recom.Recommendation;
import com.business.model.request.*;
import com.business.service.ProductService;
import com.business.service.RatingService;
import com.business.service.RecommenderService;
import com.business.service.UserService;
import com.business.utils.Constant;
import com.business.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/product")
public class ProductRestApi {

    @Resource
    private RecommenderService recommenderService;

    @Resource
    private ProductService productService;

    @Resource
    private UserService userService;

    @Resource
    private RatingService ratingService;


    /**
     * 获取热门推荐
     * @param num
     * @return
     */
    @GetMapping(value = "/hot")
    public Result getHotProducts(@RequestParam("num") int num) {
        List<Recommendation> recommendations = recommenderService.getHotRecommendations(num);
        return Result.success(productService.getRecommendProducts(recommendations));
    }

    /**
     * 获取打分最多的商品
     * @param num
     * @return
     */
    @GetMapping(value = "/rate")
    public Result getRateMoreProducts(@RequestParam("num") int num) {
        List<Recommendation> recommendations = recommenderService.getRateMoreRecommendations(num);
        return Result.success(productService.getRecommendProducts(recommendations));
    }

    /**
     * 基于物品的协同过滤
     * @param id
     * @return
     */
    @GetMapping(value = "/itemcf/{id}")
    public Result getItemCFProducts(@PathVariable("id") int id) {
        List<Recommendation> recommendations = recommenderService.getItemCFRecommendations(new ItemCFRecommendationRequest(id));
        return Result.success(productService.getRecommendProducts(recommendations));
    }

    /**
     * 基于内容的推荐
     * @param id
     * @return
     */
    @GetMapping(value = "/contentbased/{id}")
    public Result getContentBasedProducts(@PathVariable("id") int id) {
        List<Recommendation> recommendations = recommenderService.getContentBasedRecommendations(new ContentBasedRecommendationRequest(id));
        return Result.success(productService.getRecommendProducts(recommendations));
    }

    /**
     * 获取单个商品的信息
     * @param id
     * @return
     */
    @GetMapping(value = "/info/{id}")
    public Result getProductInfo(@PathVariable("id") int id) {
        return Result.success(productService.findByProductId(id));
    }

    /**
     * 模糊查询商品
     * @param query
     * @return
     */
    @GetMapping(value = "/search")
    public Result getSearchProducts(@RequestParam("query") String query) {
        try {
            query = new String(query.getBytes("ISO-8859-1"),"UTF-8");
        } catch(java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<Product> products = productService.findByProductName(query);
        return Result.success(products);
    }

    @GetMapping(value = "/rate/{id}")
    public Result rateToProduct(@PathVariable("id") int id, @RequestParam("score") Double score, @RequestParam("username") String username) {
        User user = userService.findByUsername(username);
        ProductRatingRequest request = new ProductRatingRequest(user.getUserId(), id, score);
        boolean complete = ratingService.productRating(request);
        //埋点日志
        if(complete) {
            System.out.print("=========埋点=========");
            log.info(Constant.PRODUCT_RATING_PREFIX + ":" + user.getUserId() +"|"+ id +"|"+ request.getScore() +"|"+ System.currentTimeMillis()/1000);
        }
        return Result.success("已完成评分");
    }

    // 离线推荐
    @GetMapping(value = "/offline")
    public Result getOfflineProducts(@RequestParam("username") String username,@RequestParam("num") int num) {
        User user = userService.findByUsername(username);
        List<Recommendation> recommendations = recommenderService.getCollaborativeFilteringRecommendations(new UserRecommendationRequest(user.getUserId(), num));
        List<Product> productList = new ArrayList<>();
        if(recommendations != null){
            productList = productService.getRecommendProducts(recommendations);
        }
        return Result.success(productList);
    }

    // 实时推荐
    @GetMapping(value = "/stream")
    public Result getStreamProducts(@RequestParam("username") String username,@RequestParam("num") int num) {
        User user = userService.findByUsername(username);
        List<Recommendation> recommendations = recommenderService.getStreamRecommendations(new UserRecommendationRequest(user.getUserId(), num));
        return Result.success( productService.getRecommendProducts(recommendations));
    }
}
