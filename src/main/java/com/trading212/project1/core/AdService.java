package com.trading212.project1.core;

import com.trading212.project1.core.mappers.Mappers;
import com.trading212.project1.core.models.Ad;
import com.trading212.project1.core.models.SimilaritySearchFilter;
import com.trading212.project1.repositories.AdRepository;
import com.trading212.project1.repositories.EmbeddingAdsRepository;
import com.trading212.project1.repositories.entities.AdEntity;
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
    public void deleteAd(Long adId, String partition) {
        adRepository.deleteAdById(adId);
        embeddingRepository.deleteAds(partition, List.of(adId));
    }

    @Transactional
    public void deleteAdsNotIn(List<Long> adIds, String partitionName) {
        adRepository.deleteAdsNotIn(adIds);
        embeddingRepository.deleteAdsNotInIds(partitionName, adIds);
    }


    public void saveAdsChanges() {
        embeddingRepository.flush();
        embeddingRepository.compact();
    }

    public List<Ad> getAdsWithLinksFrom(List<String> links) {
        List<Long> adIds = adRepository.getAdIdsWithLinksIn(links);
        List<Ad> ads = new ArrayList<>();
        for (var adId : adIds) {
            ads.add(Mappers.fromAdEntity(adRepository.getByAdId(adId)));
        }
        return ads;
    }




    public List<Ad> findClosestAds(List<Float> embedding, String partitionName, int topK, SimilaritySearchFilter searchFilter) {
        List<Long> recommendedAdIds = embeddingRepository.similaritySearchForAds(embedding,
            searchFilter.toFilterExpression(), partitionName, topK);

        List<Ad> recommendedAds = new ArrayList<>();
        for (var recommendedAdId : recommendedAdIds) {
            recommendedAds.add(Mappers.fromAdEntity(adRepository.getByAdId(recommendedAdId)));
        }
        return recommendedAds;
    }



    private Ad getAdById(Long adId) {
        AdEntity adEntity = adRepository.getByAdId(adId);
        return Mappers.fromAdEntity(adEntity);
    }
}
