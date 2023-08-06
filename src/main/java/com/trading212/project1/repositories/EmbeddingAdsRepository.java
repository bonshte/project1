package com.trading212.project1.repositories;

import com.trading212.project1.core.models.Ad;

import java.util.List;

public interface EmbeddingAdsRepository {

    void loadCollection();

    void compact();

    void createAds(String partitionName, List<List<Float>> embeddings, List<Ad> ads);

    List<Long> similaritySearchForAds(List<Float> searchEmbedding, String expr, String partitionName, int topK);

    void deleteAds(String partitionName, List<Long> adIds);

    void flush();

    void releaseCollection();

//    void deleteAdsNotInIds(String partitionName, List<Long> adIds);




}
