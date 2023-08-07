package com.trading212.project1.api.rest.models;

import com.trading212.project1.core.models.Ad;
import com.trading212.project1.core.models.ChatMessage;
import com.trading212.project1.core.models.ChatSession;

import java.util.ArrayList;

public class Mappers {
    private Mappers() {
        throw new RuntimeException("should not be instantiated");
    }

    public static ChatMessageResponse fromChatMessage(ChatMessage chatMessage) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setMessage(chatMessage.getSentMessage());
        chatMessageResponse.setFromUser(chatMessage.isFromUser());
        chatMessageResponse.setChatSessionId(chatMessage.getChatSessionId());
        chatMessageResponse.setAdsFound(chatMessage.isAdsFound());

        return chatMessageResponse;
    }

    public static ChatSessionResponse fromChatSession(ChatSession chatSession) {
        return new ChatSessionResponse(
                chatSession.getSessionId(),
                chatSession.getDescription()
        );
    }

    public static AdResponse fromAd(Ad ad) {
        return AdResponse.builder()
            .adId(ad.getAdId())
            .town(ad.getTown())
            .neighbourhood(ad.getNeighbourhood())
            .district(ad.getDistrict())
            .accommodationType(ad.getAccommodationType())
            .price(ad.getPrice())
            .currency(ad.getCurrency())
            .propertyProvider(ad.getPropertyProvider())
            .size(ad.getSize())
            .floor(ad.getFloor())
            .totalFloors(ad.getTotalFloors())
            .gasProvided(ad.getGasProvided())
            .thermalPowerPlantProvided(ad.getThermalPowerPlantProvided())
            .forSale(ad.getForSale())
            .features(ad.getFeatures() != null ? new ArrayList<>(ad.getFeatures()) : null)
            .phoneNumber(ad.getPhoneNumber())
            .yearBuilt(ad.getYearBuilt())
            .link(ad.getLink())
            .construction(ad.getConstruction())
            .description(ad.getDescription())
            .imageUrls(ad.getImageUrls() != null ? new ArrayList<>(ad.getImageUrls()) : null)
            .build();
    }


}
