package com.business.utils;


import com.mongodb.MongoClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Configuration
public class Configure {

    private String mongoHost;
    private int mongoPort;
    private String redisHost;

    private String esClusterName;
    private String esHost;
    private int esPort;


    public Configure(){
        this.mongoHost = LoadApplicationUtil.getYmlNew("mongo.host");
        this.mongoPort = Integer.valueOf(LoadApplicationUtil.getYmlNew("mongo.port"));
        this.redisHost = LoadApplicationUtil.getYmlNew("redis.host");

    }

    @Bean(name = "mongoClient")
    public MongoClient getMongoClient(){
        MongoClient mongoClient = new MongoClient( mongoHost , mongoPort );
        return mongoClient;
    }

    @Bean(name = "transportClient")
    public TransportClient getTransportClient() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name",esClusterName).build();
        TransportClient esClient = new PreBuiltTransportClient(settings);
        esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esHost), esPort));
        return esClient;
    }

    @Bean(name = "jedis")
    public Jedis getRedisClient() {
        Jedis jedis = new Jedis(redisHost);
        return jedis;
    }
}
