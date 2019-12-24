package com.github.atlantidanetwork.api.lib.mongo;

import com.github.atlantidanetwork.api.lib.configuration.AtlantidaConfig;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MongoConnect {

    private AtlantidaConfig mongoConfig;
    private MongoClientURI mongoUri;
    private MongoClient mongoClient;
    private MongoDatabase dataBase;
    private MongoCollection<Document> collection;

    public void connect (Plugin plugin) {
        mongoConfig = new AtlantidaConfig(plugin, "mongo.yml");
        if(!new File(plugin.getDataFolder(), "mongo.yml").exists()) {
            mongoConfig.set("mongo.user", "<user>");
            mongoConfig.set("mongo.password", "<password>");
            mongoConfig.set("mongo.cluster", "<cluster>");
            mongoConfig.set("mongo.data-base", "<data-base>");
            mongoConfig.set("mongo.collection", "<collection>");
            mongoConfig.saveConfig();
        }
        String user = mongoConfig.getString("mongo.user");
        String pass = mongoConfig.getString("mongo.password");
        String cluster = mongoConfig.getString("mongo.cluster");
        String dataBaseName = mongoConfig.getString("mongo.data-base");
        String collectionName = mongoConfig.getString("mongo.collection");
        mongoUri = new MongoClientURI("mongodb+srv://" + user + ":" + pass + "@" + cluster + "-0fu2q.gcp.mongodb.net/test");
        mongoClient = new MongoClient(mongoUri);
        dataBase = mongoClient.getDatabase(dataBaseName);
        collection = dataBase.getCollection(collectionName);
    }


    public MongoClientURI getMongoUri() {
        return mongoUri;
    }

    public void setMongoUri(MongoClientURI mongoUri) {
        this.mongoUri = mongoUri;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getDataBase() {
        return dataBase;
    }

    public void setDataBase(MongoDatabase dataBase) {
        this.dataBase = dataBase;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public void setCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public AtlantidaConfig getMongoConfig() {
        return mongoConfig;
    }

    public void setMongoConfig(AtlantidaConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }
}
