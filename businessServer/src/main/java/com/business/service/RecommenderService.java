package com.business.service;

import com.business.model.recom.Recommendation;
import com.business.model.request.ContentBasedRecommendationRequest;
import com.business.model.request.UserRecommendationRequest;
import com.business.utils.Constant;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommenderService {

    @Resource
    private MongoClient mongoClient;


    public List<Recommendation> getHotRecommendations(int num) {
        // 获取热门的条目
        MongoCollection<Document> rateMoreMoviesRecentlyCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATE_MORE_PRODUCTS_RECENTLY_COLLECTION);
        FindIterable<Document> documents = rateMoreMoviesRecentlyCollection.find().sort(Sorts.descending("yearmonth")).limit(num);

        List<Recommendation> recommendations = new ArrayList<>();
        if(documents != null){
            for (Document document : documents) {
                recommendations.add(new Recommendation(document.getInteger("productId"), 0D));
            }
        }
        return recommendations;
    }

    public List<Recommendation> getRateMoreRecommendations(int num) {

        // 获取评分最多的条目
        MongoCollection<Document> rateMoreProductsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_RATE_MORE_PRODUCTS_COLLECTION);
        FindIterable<Document> documents = rateMoreProductsCollection.find().sort(Sorts.descending("count")).limit(num);

        List<Recommendation> recommendations = new ArrayList<>();
        if(documents != null){
            for (Document document : documents) {
                recommendations.add(new Recommendation(document.getInteger("productId"), 0D));
            }
        }
        return recommendations;
    }

    public List<Recommendation> getItemCFRecommendations(int pid) {
        MongoCollection<Document> itemCFProductsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_ITEMCF_COLLECTION);
        Document document = itemCFProductsCollection.find(new Document("productId", pid)).first();

        List<Recommendation> recommendations = new ArrayList<>();
        if(document != null){
            ArrayList<Document> recs = document.get("recs", ArrayList.class);

            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getInteger("productId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }

    public List<Recommendation> getContentBasedRecommendations(ContentBasedRecommendationRequest request) {
        MongoCollection<Document> contentBasedProductsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_CONTENTBASED_COLLECTION);
        Document document = contentBasedProductsCollection.find(new Document("productId", request.getId())).first();

        List<Recommendation> recommendations = new ArrayList<>();
        if(document != null){
            ArrayList<Document> recs = document.get("recs", ArrayList.class);

            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getInteger("productId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }

    public List<Recommendation> getCollaborativeFilteringRecommendations(UserRecommendationRequest request) {
        MongoCollection<Document> userRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_RECS_COLLECTION);
        Document document = userRecsCollection.find(new Document("userId", request.getUserId())).first();

        if(document == null){
            return null;
        }
        List<Recommendation> recommendations = new ArrayList<>();
        if(document != null){
            ArrayList<Document> recs = document.get("recs", ArrayList.class);

            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getInteger("productId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }

    public List<Recommendation> getStreamRecommendations(UserRecommendationRequest request) {
        MongoCollection<Document> userRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_STREAM_RECS_COLLECTION);
        Document document = userRecsCollection.find(new Document("userId", request.getUserId())).first();

        List<Recommendation> recommendations = new ArrayList<>();
        if(document != null){
            ArrayList<Document> recs = document.get("recs", ArrayList.class);

            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getInteger("productId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }
}