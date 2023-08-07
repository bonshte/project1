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

    /*
    '4', '1', 'град Бургас', 'Меден рудник - зона В', NULL, 'TWO_ROOM', '46343', 'EURO', 'Брокер: Петя Маринова', '61', '7', '8', '0', '0', '0884 178 791', NULL, 'https://www.imot.bg/1b168448681260903', NULL, 'Агенция SUPER ИМОТИ: www.suprimmo.bg Представяме за продажба двустаен апартамент в нов комплекс с удобна локация до Терминала и новия парк в \'Меден рудник - зона В\'. Пред Акт 15. Районът е един от най-бързо развиващи се, с множество модерни сгради, кв. \'Меден рудник - зона В\' в Бургас. Достъпът до сградите е бърз и лесен, от две главни улици. Тук ще намерите всичко необходимо за комфортното живеене, с отлична инфраструктура, разнообразни магазини, заведения и транспорт. Сградата граничи с новоизграждащия се на няколко декара зелен парк и спортен комплекс с алеи, детски кътове и зони за отдих, спортна зала, футболно игрище и паркинг. Имотите са разпределени на 8 етажа, с партерни гаражи (в наличност). В двора на сградата ще бъдат обособени алеи, места за отдих, както и зона за паркиране и паркоместа. Офертните цени са за Секция Е. В изграждането на сградата са използвани материали с високо качество и най-актуални технологии за строителството. Имотите се продават в степен на завършеност по БДС. Свържете се с нас за професионална консултация относно интересуващия Ви тип имот в този модерен комплекс в Бургас! За повече информация свържете се с нас и цитирайте референтния номер на имота. Моля, кажете, че сте видeли обявата в този сайт. Референтен номер: LXH-112947 Тел: 0884 178 791, 056 707 935 Отговорен брокер:Петя Маринова Без комисиона от купувача  '

     */
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
