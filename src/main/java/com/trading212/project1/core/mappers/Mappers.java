package com.trading212.project1.core.mappers;

import com.trading212.project1.core.models.Ad;
import com.trading212.project1.core.models.ChatMessage;
import com.trading212.project1.core.models.ChatSession;
import com.trading212.project1.core.models.User;
import com.trading212.project1.core.models.scraping.ScrapedAd;
import com.trading212.project1.repositories.entities.*;

import java.util.List;
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
            chatMessageEntity.getTimeSent()
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




}
