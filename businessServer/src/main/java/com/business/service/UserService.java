package com.business.service;

import com.business.model.domain.User;
import com.business.utils.Constant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class UserService {


    @Resource
    private MongoClient mongoClient;

    @Resource
    private ObjectMapper objectMapper;

    private MongoCollection<Document> userCollection;

    private MongoCollection<Document> getUserCollection(){
        if(null == userCollection)
            userCollection =  mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_COLLECTION);
        return userCollection;
    }

    public boolean registerUser(User request){
        User user = new User();
        BeanUtils.copyProperties(request,user);
        try{
            getUserCollection().insertOne(Document.parse(objectMapper.writeValueAsString(user)));
            return true;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return false;
        }
    }


    private User documentToUser(Document document){
        try{
            return objectMapper.readValue(JSON.serialize(document),User.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkUserExist(String username){
        return null != findByUsername(username);
    }

    public User findByUsername(String username){
        Document user = getUserCollection().find(new Document("name",username)).first();
        if(null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    public boolean updateUser(User user){
        getUserCollection().updateOne(Filters.eq("userId", user.getUserId()), new Document().append("$set",new Document("first", user.isFirst())));
        getUserCollection().updateOne(Filters.eq("userId", user.getUserId()), new Document().append("$set",new Document("prefGenres", user.getPrefGenres())));
        return true;
    }

}
