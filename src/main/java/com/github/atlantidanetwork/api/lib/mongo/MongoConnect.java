package com.github.atlantidanetwork.api.lib.mongo;

import com.github.atlantidanetwork.api.lib.configuration.AtlantidaConfig;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MongoConnect {

    private AtlantidaConfig mongoConfig;
    private MongoClientURI mongoUri;
    private MongoClient mongoClient;
    private MongoDatabase dataBase;

    public void connect (Plugin plugin) {
        mongoConfig = new AtlantidaConfig(plugin, "mongo.yml");
        if(!new File(plugin.getDataFolder(), "mongo.yml").exists()) {
            mongoConfig.set("uri", "<mongo-uri do seu banco de dados>");
            mongoConfig.set("mongo.data-base", "<sua data-base>");
            mongoConfig.saveConfig();
        }
        try {
            String dataBaseName = mongoConfig.getString("mongo.data-base");
            mongoUri = new MongoClientURI("mongo.uri");
            mongoClient = new MongoClient(mongoUri);
            dataBase = mongoClient.getDatabase(dataBaseName);
            Bukkit.getConsoleSender().sendMessage("§2[MongoDB] §3Banco de dados §6" + dataBaseName + " §3conectado com sucesso.");
        }catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§2[MongoDB] §4Nao foi possivel se conectar ao banco de dados.");
            e.printStackTrace();
        }
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

    public AtlantidaConfig getMongoConfig() {
        return mongoConfig;
    }

    public void setMongoConfig(AtlantidaConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }
}
