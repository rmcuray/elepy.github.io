package com.elepy.mongo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import org.jongo.Jongo;

public class DefaultMongoDao<T> extends MongoDao<T> {

    private final DB db;
    private final Class<T> classType;
    private final String collectionName;
    private final ObjectMapper objectMapper;
    private final Jongo jongo;


    public DefaultMongoDao(final DB db, final String collectionName, final Class<T> classType) {
        this(db, collectionName, classType, new ObjectMapper());
    }


    public DefaultMongoDao(final DB db, final String collectionName, final Class<T> classType, ObjectMapper objectMapper) {
        this.db = db;
        this.classType = classType;
        this.collectionName = collectionName.replaceAll("/", "");
        this.objectMapper = objectMapper;
        this.jongo = new Jongo(db(), new ElepyMapper(this));

    }

    @Override
    Jongo getJongo() {
        return jongo;
    }

    @Override
    public Class<T> modelType() {
        return classType;
    }

    @Override
    public String mongoCollectionName() {
        return collectionName;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public DB db() {
        return db;
    }

}
