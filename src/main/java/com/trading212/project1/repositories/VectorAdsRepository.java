package com.trading212.project1.repositories;

import com.trading212.project1.core.models.AdStub;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

import java.util.List;

public interface VectorAdsRepository {
    String ID_FIELD = "ad_id";
    String EMBEDDING_FIELD = "property_embedding";
    String TOWN_FIELD = "town";
    String NEIGHBOURHOOD_FIELD = "neighbourhood";
    String PRICE_FIELD = "price";
    String ACCOMMODATION_TYPE_FIELD = "accommodation_type";

    void createCollection(long timeout);

    void dropCollection();

    void loadCollection();

    void createPartition(String partitionName);

    void compact();

    void insertProperties(String partitionName, List<List<Float>> embeddings, List<AdStub> ads);

    void createIndex(String fieldName, String indexName, IndexType indexType,
                     MetricType metricType, String indexParam);

    void releasePartition(String partitionName);

    void delete(String partitionName, String expr);

    void dropIndex(String indexName);

    void releaseCollection();




}
