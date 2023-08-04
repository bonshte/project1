package com.trading212.project1.core;

import com.beust.jcommander.Strings;
import com.trading212.project1.core.mappers.ImotBGAdMapper;
import com.trading212.project1.core.models.Ad;
import com.trading212.project1.core.models.scraping.ScrapedAd;
import com.trading212.project1.core.models.scraping.ScrapeConfig;
import com.trading212.project1.core.models.scraping.ScrapingResult;
import com.trading212.project1.repositories.AdRepository;
import com.trading212.project1.repositories.mariadb.MariaDBAdRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class ScrapeCron {
    private final ImotBGScrapingService scrapingService;
    private final DeepLTranslationService translationService;
    private final AdaEmbeddingService embeddingService;
    private final AdService adService
    private final GPTService gptService;


    public ScrapeCron(ImotBGScrapingService scrapingService, DeepLTranslationService translationService,
                      AdaEmbeddingService embeddingService, GPTService gptService, AdService adService) {
        this.scrapingService = scrapingService;
        this.translationService = translationService;
        this.embeddingService = embeddingService;
        this.gptService = gptService;
        this.adService = adService;
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




    @Transactional
    @Scheduled(cron = "0 01 * * * *")
    public void scrapeImotBG() {
        ScrapingResult rentScrapeResult = scrapingService.scrape(BURGAS_RENT_SCRAPE_CONFIG);
        List<ScrapedAd> scrapedApartmentsForRent = rentScrapeResult.getScrapedAds();
        List<String> adLinks = scrapedApartmentsForRent.stream().map(ScrapedAd::getLink).toList();

        List<Ad> persistentAds = adService.getAdsWithLinksFrom(adLinks);

        // Create Maps
        Map<String, Ad> adMap = new HashMap<>();
        Map<String, ScrapedAd> scrapedAdMap = new HashMap<>();

// Populate Maps
        for (Ad ad : persistentAds) {
            adMap.put(ad.getLink(), ad);
        }

        for (ScrapedAd scrapedAd : scrapedApartmentsForRent) {
            scrapedAdMap.put(scrapedAd.getLink(), scrapedAd);
        }

// Create Lists for Ads and ScrapedAds that meet the criteria
        List<Ad> adsThatDoNotCompare = new ArrayList<>();
        List<ScrapedAd> scrapedAdsThatCompare = new ArrayList<>();

// Iterate over Maps and compare Ads
        for (String link : adMap.keySet()) {
            if (scrapedAdMap.containsKey(link)) {
                if (!isSameProperty(adMap.get(link), scrapedAdMap.get(link))) {
                    adsThatDoNotCompare.add(adMap.get(link));
                }
            }
        }

// Iterate over Maps and compare ScrapedAds
        for (String link : scrapedAdMap.keySet()) {
            if (adMap.containsKey(link)) {
                if (compare(adMap.get(link), scrapedAdMap.get(link))) {
                    scrapedAdsThatCompare.add(scrapedAdMap.get(link));
                }
            }
        }



//        ScrapingResult rentScrapeResult = scrapingService.scrape(BURGAS_RENT_SCRAPE_CONFIG);
//        List<ScrapedAd> apartmentsForRent = rentScrapeResult.getScrapedAds();
//        for (var apartment : apartmentsForRent) {
//            System.out.println(apartment);
//        }
//
//        apartmentsForRent = apartmentsForRent.stream().limit(3).toList();
//
//        List<ScrapedAd> translatedApartments = apartmentsForRent.stream()
//                .map(this::translateAd)
//                .toList();
//
//        for (var translatedApartment : translatedApartments) {
//            System.out.println("old description:" + translatedApartment.getDescription());
//            GPTService.GPTMessageDTO messageDTO = gptService.summarizeDescription(translatedApartment.getDescription());
//            translatedApartment.setDescription(messageDTO.getContent());
//        }
//
//        List<String> apartmentDescriptionsForEmbedding = translatedApartments.stream()
//                .map(this::toEmbeddableText)
//                .toList();
//
//
//
//        List<List<Float>> embeddings = apartmentDescriptionsForEmbedding.stream()
//                .map(embeddingService::embedWithAda)
//                .toList();
//
//        for (int i = 0; i < 3 && i < apartmentsForRent.size(); ++i) {
//            System.out.println(translatedApartments.get(i));
//            System.out.println(apartmentDescriptionsForEmbedding.get(i));
//        }

    }


//    private String toEmbeddableText(ScrapedAd scrapedAd) {
//        return "Apartment in" +
//                (scrapedAd.getDistrict() != null ? scrapedAd.getDistrict() : "") +
//                (scrapedAd.getTown() != null ? " " + scrapedAd.getTown() + "," : "") +
//                (scrapedAd.getNeighbourhood() != null ? " neighbourhood " + scrapedAd.getNeighbourhood() + "," : "") +
//                (scrapedAd.getAccommodationType() != null ? " apartment type " + scrapedAd.getAccommodationType().toDescriptionString() + "," : "") +
//                (scrapedAd.getPrice() != null ? " price " + scrapedAd.calculateInBGN().intValue() + "," : "") +
//                (scrapedAd.getPropertyProvider() != null ? " " + scrapedAd.getPropertyProvider() + "," : "") +
//                (scrapedAd.getSize() != null ? " square meters " + scrapedAd.getSize() + "," : "") +
//                (scrapedAd.isGasProvided() ? " has gas heating," : "") +
//                (scrapedAd.isThermalPowerPlantProvided() ? " has thermal power plant heating," : "") +
//                (scrapedAd.getFeatures() != null ? " features " + Strings.join( " ", scrapedAd.getFeatures()) + "," : "") +
//                (scrapedAd.getDescription() != null ? " " + scrapedAd.getDescription() : "");
//
//    }

//    private ScrapedAd translateAd(ScrapedAd scrapedAd) {
//        return new ScrapedAd(
//                scrapedAd.getId(),
//                translationService.translateToEnglish(scrapedAd.getTown()),
//                scrapedAd.getNeighbourhood() != null ?
//                        translationService.translateToEnglish(scrapedAd.getNeighbourhood()) : null,
//                scrapedAd.getDistrict() != null ?
//                        translationService.translateToEnglish(scrapedAd.getDistrict()) : null,
//                scrapedAd.getAccommodationType(),
//                scrapedAd.getPrice(),
//                scrapedAd.getCurrency(),
//                scrapedAd.getPropertyProvider()  != null ?
//                        translationService.translateToEnglish(scrapedAd.getPropertyProvider()) : null,
//                scrapedAd.getSize(),
//                scrapedAd.getFloor(),
//                scrapedAd.getTotalFloors(),
//                scrapedAd.isGasProvided(),
//                scrapedAd.isThermalPowerPlantProvided(),
//                scrapedAd.getFeatures() != null ?
//                        translationService.translateToEnglish(scrapedAd.getFeatures()) : scrapedAd.getFeatures(),
//                scrapedAd.getPhoneNumber(),
//                scrapedAd.getYearBuilt(),
//                scrapedAd.getLink(),
//                scrapedAd.getConstruction() != null ?
//                        translationService.translateToEnglish(scrapedAd.getConstruction()) : null,
//                scrapedAd.getDescription() != null ?
//                        translationService.translateToEnglish(scrapedAd.getDescription()) : null,
//                scrapedAd.getImageUrls()
//        );
//    }

    private boolean isSameProperty(ScrapedAd scrapedAd, Ad savedAd, boolean forSale) {
        if (!Objects.equals(savedAd.getTown(), scrapedAd.getTown())) return false;
        if (!Objects.equals(savedAd.getNeighbourhood(), scrapedAd.getNeighbourhood())) return false;
        if (!Objects.equals(savedAd.getDistrict(), scrapedAd.getDistrict())) return false;
        if (!Objects.equals(savedAd.getAccommodationType(), scrapedAd.getAccommodationType())) return false;
        if (!Objects.equals(savedAd.getPrice(), scrapedAd.getPrice())) return false;
        if (!Objects.equals(savedAd.getCurrency(), scrapedAd.getCurrency())) return false;
        if (!Objects.equals(savedAd.getPropertyProvider(), scrapedAd.getPropertyProvider())) return false;
        if (!Objects.equals(savedAd.getSize(), scrapedAd.getSize())) return false;
        if (!Objects.equals(savedAd.getFloor(), scrapedAd.getFloor())) return false;
        if (!Objects.equals(savedAd.getTotalFloors(), scrapedAd.getTotalFloors())) return false;
        if (!Objects.equals(savedAd.getGasProvided(), scrapedAd.isGasProvided())) return false;
        if (!Objects.equals(savedAd.getForSale(), forSale)) return false;
        if (!Objects.equals(savedAd.getThermalPowerPlantProvided(), scrapedAd.isThermalPowerPlantProvided())) return false;
        if (!Objects.equals(savedAd.getPhoneNumber(), scrapedAd.getPhoneNumber())) return false;
        if (!Objects.equals(savedAd.getYearBuilt(), scrapedAd.getYearBuilt())) return false;
        if (!Objects.equals(savedAd.getConstruction(), scrapedAd.getConstruction())) return false;
        if (!Objects.equals(savedAd.getDescription(), scrapedAd.getDescription())) return false;

        Set<String> savedAdFeatures = new HashSet<>(savedAd.getFeatures());
        Set<String> scrapedAdFeatures = new HashSet<>(scrapedAd.getFeatures());
        if (!savedAdFeatures.equals(scrapedAdFeatures)) return false;

        Set<String> savedAdImageUrls = new HashSet<>(savedAd.getImageUrls());
        Set<String> scrapedAdImageUrls = new HashSet<>(scrapedAd.getImageUrls());
        if (!savedAdImageUrls.equals(scrapedAdImageUrls)) return false;

        return true;
    }
}
