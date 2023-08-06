package com.trading212.project1.core;

import com.trading212.project1.core.mappers.Mappers;
import com.trading212.project1.core.models.Ad;
import com.trading212.project1.core.models.SimilaritySearchFilter;
import com.trading212.project1.core.models.scraping.ScrapedAd;
import com.trading212.project1.repositories.AdRepository;
import com.trading212.project1.repositories.EmbeddingAdsRepository;
import com.trading212.project1.repositories.entities.AdEntity;
import com.trading212.project1.repositories.milvus.MilvusAdsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdService {

    private final AdRepository adRepository;
    private final EmbeddingAdsRepository embeddingRepository;

    public AdService(AdRepository adRepository, EmbeddingAdsRepository embeddingRepository) {
        this.adRepository = adRepository;
        this.embeddingRepository = embeddingRepository;
    }

    @Transactional
    public void deleteAd(Long adId, boolean forSale) {
        adRepository.deleteAdById(adId);
        embeddingRepository.deleteAds(
            forSale ? MilvusAdsRepository.SALE_PARTITION : MilvusAdsRepository.RENT_PARTITION
            , List.of(adId));
    }


    @Transactional
    public void createAds(List<ScrapedAd> scrapedAds, List<List<Float>> embeddings,
                          List<ScrapedAd> translatedScrapedAds, boolean forSale) {
        if (scrapedAds.size() != embeddings.size()) {
            throw new RuntimeException("miss in scrapedAds and embeddings");
        }
        List<Ad> savedAds = new ArrayList<>();
        for (var scrapedAd : scrapedAds) {
            AdEntity savedAdEntity = adRepository.createAd(
                scrapedAd.getTown(),
                scrapedAd.getNeighbourhood(),
                scrapedAd.getDistrict(),
                scrapedAd.getAccommodationType(),
                scrapedAd.getPrice(),
                scrapedAd.getCurrency(),
                scrapedAd.getPropertyProvider(),
                scrapedAd.getSize(),
                scrapedAd.getFloor(),
                scrapedAd.getTotalFloors(),
                scrapedAd.isGasProvided(),
                scrapedAd.isThermalPowerPlantProvided(),
                scrapedAd.getPhoneNumber(),
                scrapedAd.getYearBuilt(),
                scrapedAd.getLink(),
                scrapedAd.getConstruction(),
                scrapedAd.getDescription(),
                forSale,
                scrapedAd.getFeatures(),
                scrapedAd.getImageUrls()
            );
            savedAds.add(Mappers.fromAdEntity(savedAdEntity));
        }

        List<Ad> translatedAds = Mappers.generateTranslatedAds(savedAds, translatedScrapedAds);
        for (var translatedAd : translatedAds) {
            translatedAd.setPrice(translatedAd.calculateInBGN());
        }

        //console
        System.out.println("ads passing to milvus");
        for (var ad : translatedAds) {
            System.out.println(ad);
        }
        embeddingRepository.createAds(
            forSale ? MilvusAdsRepository.SALE_PARTITION : MilvusAdsRepository.RENT_PARTITION,
            embeddings,
            translatedAds
        );
    }

    public List<Ad> getAds(List<Long> adIds) {
        List<Ad> ads = new ArrayList<>();
        for (var adId : adIds) {
            AdEntity adEntity = adRepository.getByAdId(adId);
            ads.add(Mappers.fromAdEntity(adEntity));
        }
        return ads;
    }


    public void saveAdsChanges() {
        embeddingRepository.flush();
        embeddingRepository.compact();
    }

    public List<Ad> getAllAdsByOffer(boolean forSale) {
        List<AdEntity> adEntities = adRepository.getAllAdsByOffer(forSale);
        return adEntities.stream()
            .map(Mappers::fromAdEntity)
            .toList();
    }

    public Ad getAdById(Long adId) {
        AdEntity adEntity = adRepository.getByAdId(adId);
        return Mappers.fromAdEntity(adEntity);
    }

    //check this later
    public List<Ad> findClosestAds(List<Float> embedding, String partitionName,
                                   int topK, SimilaritySearchFilter searchFilter) {

        List<Long> recommendedAdIds = embeddingRepository.similaritySearchForAds(embedding,
            searchFilter.toFilterExpression(), partitionName, topK);

        List<Ad> recommendedAds = new ArrayList<>();
        for (var recommendedAdId : recommendedAdIds) {
            recommendedAds.add(Mappers.fromAdEntity(adRepository.getByAdId(recommendedAdId)));
        }
        System.out.println("found stuff");
        return recommendedAds;
    }

    @Transactional
    public void deleteAdsIn(List<Long> adIds, boolean forSale) {
        for (var adId : adIds) {
            deleteAd(adId, forSale);
        }
    }
}
