package com.trading212.project1.configuration;

import com.trading212.project1.repositories.milvus.MilvusAdsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MilvusCollectionLoader implements CommandLineRunner {

    private final MilvusAdsRepository milvusAdsRepository;

    public MilvusCollectionLoader(MilvusAdsRepository milvusAdsRepository) {

        this.milvusAdsRepository = milvusAdsRepository;
        this.milvusAdsRepository.query();
    }

    @Override
    public void run(String... args) {
        milvusAdsRepository.query();
        milvusAdsRepository.setUp();
    }
}