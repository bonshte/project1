package com.trading212.project1.core.mappers;

import com.trading212.project1.core.models.*;
import com.trading212.project1.core.models.scraping.ScrapedAd;
import com.trading212.project1.repositories.entities.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Mappers {
    public static User fromUserEntity(UserEntity userEntity) {
        return new User(
            userEntity.getId(),
            userEntity.getEmail(),
            userEntity.getPassword(),
            userEntity.getPremiumUntil(),
            userEntity.getRole()
        );
    }

    public static ChatMessage fromChatMessageEntity(ChatMessageEntity chatMessageEntity) {
        return new ChatMessage(
            chatMessageEntity.getChatSessionId(),
            chatMessageEntity.getSentMessage(),
            chatMessageEntity.getTranslatedMessage(),
            chatMessageEntity.isFromUser(),
            chatMessageEntity.getTimestamp(),
            chatMessageEntity.isAdsFound()
        );
    }

    public static ChatSession fromChatSessionEntity(ChatSessionEntity chatSessionEntity) {
        return new ChatSession(
            chatSessionEntity.getSessionId(),
            chatSessionEntity.getDescription(),
            chatSessionEntity.getUserId()
        );
    }

    public static Ad fromAdEntity(AdEntity adEntity) {
        Ad ad = new Ad();

        ad.setAdId(adEntity.getAdId());
        ad.setTown(adEntity.getTown());
        ad.setNeighbourhood(adEntity.getNeighbourhood());
        ad.setDistrict(adEntity.getDistrict());
        ad.setAccommodationType(adEntity.getAccommodationType());
        ad.setPrice(adEntity.getPrice());
        ad.setCurrency(adEntity.getCurrency());
        ad.setPropertyProvider(adEntity.getPropertyProvider());
        ad.setSize(adEntity.getSize());
        ad.setFloor(adEntity.getFloor());
        ad.setTotalFloors(adEntity.getTotalFloors());
        ad.setGasProvided(adEntity.getGasProvided());
        ad.setThermalPowerPlantProvided(adEntity.getThermalPowerPlantProvided());
        ad.setForSale(adEntity.getForSale());
        ad.setFeatures(adEntity.getFeatures());
        ad.setPhoneNumber(adEntity.getPhoneNumber());
        ad.setYearBuilt(adEntity.getYearBuilt());
        ad.setLink(adEntity.getLink());
        ad.setConstruction(adEntity.getConstruction());
        ad.setDescription(adEntity.getDescription());
        ad.setImageUrls(adEntity.getImageUrls());
        return ad;
    }

    public static ChatSessionRecommendation fromChatSessionRecommendationEntity(ChatSessionRecommendationEntity chatSessionRecommendationEntity) {
        ChatSessionRecommendation chatSessionRecommendation = new ChatSessionRecommendation();
        chatSessionRecommendation.setChatSessionId(chatSessionRecommendationEntity.getChatSessionId());
        chatSessionRecommendation.setRecommendedAt(chatSessionRecommendationEntity.getRecommendedAt());
        chatSessionRecommendation.setUserId(chatSessionRecommendationEntity.getUserId());
        chatSessionRecommendation.setForSale(chatSessionRecommendation.isForSale());
        chatSessionRecommendation.setAdId(chatSessionRecommendation.getAdId());

        return chatSessionRecommendation;
    }

    public static List<Ad> generateTranslatedAds(List<Ad> ads, List<ScrapedAd> translatedVersions) {

        Map<String, Ad> adMap = ads.stream().collect(Collectors.toMap(Ad::getLink, ad -> ad));

        List<Ad> combinedList = translatedVersions.stream().map(translatedAd -> {

            Ad matchingAd = adMap.get(translatedAd.getLink());

            return Ad.builder()
                .adId(matchingAd.getAdId())
                .town(translatedAd.getTown())
                .neighbourhood(translatedAd.getNeighbourhood())
                .district(translatedAd.getDistrict())
                .accommodationType(translatedAd.getAccommodationType())
                .price(translatedAd.getPrice())
                .currency(translatedAd.getCurrency())
                .propertyProvider(translatedAd.getPropertyProvider())
                .size(translatedAd.getSize())
                .floor(translatedAd.getFloor())
                .totalFloors(translatedAd.getTotalFloors())
                .gasProvided(translatedAd.isGasProvided())
                .thermalPowerPlantProvided(translatedAd.isThermalPowerPlantProvided())
                .forSale(matchingAd.getForSale())
                .features(translatedAd.getFeatures())
                .phoneNumber(translatedAd.getPhoneNumber())
                .yearBuilt(translatedAd.getYearBuilt())
                .link(translatedAd.getLink())
                .construction(translatedAd.getConstruction())
                .description(translatedAd.getDescription())
                .imageUrls(translatedAd.getImageUrls())
                .build();
        }).collect(Collectors.toList());

        return combinedList;
    }

}
