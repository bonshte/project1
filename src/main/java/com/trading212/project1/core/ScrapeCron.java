package com.trading212.project1.core;

import com.beust.jcommander.Strings;
import com.trading212.project1.core.mappers.ImotBGAdMapper;
import com.trading212.project1.core.models.AdStub;
import com.trading212.project1.core.models.scraping.ScrapeConfig;
import com.trading212.project1.core.models.scraping.ScrapingResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScrapeCron {
    private final ImotBGScrapingService scrapingService;
    private final DeepLTranslationService translationService;
    private final AdaEmbeddingService embeddingService;
    private final GPTService gptService;

    public ScrapeCron(ImotBGScrapingService scrapingService, DeepLTranslationService translationService,
                      AdaEmbeddingService embeddingService, GPTService gptService) {
        this.scrapingService = scrapingService;
        this.translationService = translationService;
        this.embeddingService = embeddingService;
        this.gptService = gptService;
    }

    private static final ScrapeConfig BURGAS_PURCHASE_SCRAPE_CONFIG = new ScrapeConfig(
            ImotBGUtils.PURCHASE_SCRAPE_URL,
            ImotBGUtils::getIdForPurchaseAccommodation,
            ImotBGAdMapper::fromDocument,
            ImotBGUtils.BURGAS_PURCHASE_SEARCH_FILTERS);
    private static final ScrapeConfig BURGAS_RENT_SCRAPE_CONFIG = new ScrapeConfig(
            ImotBGUtils.RENT_SCRAPE_URL,
            ImotBGUtils::getIdForRentAccommodation,
            ImotBGAdMapper::fromDocument,
            ImotBGUtils.BURGAS_RENT_SEARCH_FILTERS
    );


    @Scheduled(cron = "0 54 * * * *")
    public void scrapeImotBG() {
        ScrapingResult rentScrapeResult = scrapingService.scrape(BURGAS_RENT_SCRAPE_CONFIG);
        List<AdStub> apartmentsForRent = rentScrapeResult.getScrapedAds();
        for (var apartment : apartmentsForRent) {
            System.out.println(apartment);
        }

        apartmentsForRent = apartmentsForRent.stream().limit(3).toList();

        List<AdStub> translatedApartments = apartmentsForRent.stream()
                .map(this::translateAd)
                .toList();

        for (var translatedApartment : translatedApartments) {
            System.out.println("old description:" + translatedApartment.getDescription());
            GPTService.GPTMessageDTO messageDTO = gptService.summarizeDescription(translatedApartment.getDescription());
            translatedApartment.setDescription(messageDTO.getContent());
        }

        List<String> apartmentDescriptionsForEmbedding = translatedApartments.stream()
                .map(this::toEmbeddableText)
                .toList();



        List<List<Float>> embeddings = apartmentDescriptionsForEmbedding.stream()
                .map(embeddingService::embedWithAda)
                .toList();

        for (int i = 0; i < 3 && i < apartmentsForRent.size(); ++i) {
            System.out.println(translatedApartments.get(i));
            System.out.println(apartmentDescriptionsForEmbedding.get(i));
        }

    }


    private String toEmbeddableText(AdStub adStub) {
        return "Apartment in" +
                (adStub.getDistrict() != null ? adStub.getDistrict() : "") +
                (adStub.getTown() != null ? " " + adStub.getTown() + "," : "") +
                (adStub.getNeighbourhood() != null ? " neighbourhood " + adStub.getNeighbourhood() + "," : "") +
                (adStub.getAccommodationType() != null ? " apartment type " + adStub.getAccommodationType().toDescriptionString() + "," : "") +
                (adStub.getPrice() != null ? " price " + adStub.calculateInBGN().intValue() + "," : "") +
                (adStub.getPropertyProvider() != null ? " " + adStub.getPropertyProvider() + "," : "") +
                (adStub.getSize() != null ? " square meters " + adStub.getSize() + "," : "") +
                (adStub.isGasProvided() ? " has gas heating," : "") +
                (adStub.isThermalPowerPlantProvided() ? " has thermal power plant heating," : "") +
                (adStub.getFeatures() != null ? " features " + Strings.join( " ", adStub.getFeatures()) + "," : "") +
                (adStub.getDescription() != null ? " " + adStub.getDescription() : "");

    }

    private AdStub translateAd(AdStub adStub) {
        return new AdStub(
                adStub.getId(),
                translationService.translateToEnglish(adStub.getTown()),
                adStub.getNeighbourhood() != null ?
                        translationService.translateToEnglish(adStub.getNeighbourhood()) : null,
                adStub.getDistrict() != null ?
                        translationService.translateToEnglish(adStub.getDistrict()) : null,
                adStub.getAccommodationType(),
                adStub.getPrice(),
                adStub.getCurrency(),
                adStub.getPropertyProvider()  != null ?
                        translationService.translateToEnglish(adStub.getPropertyProvider()) : null,
                adStub.getSize(),
                adStub.getFloor(),
                adStub.getTotalFloors(),
                adStub.isGasProvided(),
                adStub.isThermalPowerPlantProvided(),
                adStub.getFeatures() != null ?
                        translationService.translateToEnglish(adStub.getFeatures()) : adStub.getFeatures(),
                adStub.getPhoneNumber(),
                adStub.getYearBuilt(),
                adStub.getLink(),
                adStub.getConstruction() != null ?
                        translationService.translateToEnglish(adStub.getConstruction()) : null,
                adStub.getDescription() != null ?
                        translationService.translateToEnglish(adStub.getDescription()) : null,
                adStub.getImageUrls()
        );
    }
}
