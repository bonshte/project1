package com.trading212.project1.core;

import com.trading212.project1.core.mappers.ImotBGAdMapper;
import com.trading212.project1.core.models.Ad;
import com.trading212.project1.core.models.scraping.ScrapedAd;
import com.trading212.project1.core.models.scraping.ScrapeConfig;
import com.trading212.project1.core.models.scraping.ScrapingResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class ScrapeCron {
    private final ImotBGScrapingService scrapingService;
    private final DeepLTranslationService translationService;
    private final AdaEmbeddingService embeddingService;
    private final AdService adService;
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





    @Scheduled(cron = "0 0 3 * * *")
    public void scrape() {
        renewApartmentData(BURGAS_RENT_SCRAPE_CONFIG, false);
        renewApartmentData(BURGAS_PURCHASE_SCRAPE_CONFIG, true);
    }

    @Transactional
    private void renewApartmentData(ScrapeConfig scrapeConfig, boolean forSale) {
        ScrapingResult rentScrapeResult = scrapingService.scrape(scrapeConfig);
        List<ScrapedAd> scrapedApartments = rentScrapeResult.getScrapedAds();

        List<Ad> savedAds = adService.getAllAdsByOffer(forSale);
        List<Ad> activeAds = getActiveAds(savedAds, scrapedApartments);
        List<Ad> inactiveAds = getInactiveAds(savedAds, scrapedApartments);
        System.out.println("active ads");
        printList(activeAds);
        System.out.println("inactive ads");
        printList(inactiveAds);

        List<Ad> activeButEdited = getActiveButEdited(activeAds, scrapedApartments, forSale);
        System.out.println("active but edited");
        printList(activeButEdited);

        List<Ad> adsToDelete = new ArrayList<>();
        adsToDelete.addAll(inactiveAds);
        adsToDelete.addAll(activeButEdited);
        adService.deleteAdsIn(adsToDelete.stream().map(Ad::getAdId).toList(), forSale);


        List<Ad> activeNotEdited = getActiveNotEdited(activeAds, activeButEdited);
        System.out.println("active but not edited");
        printList(activeNotEdited);
        List<ScrapedAd> scrapedAdsToSave = getBrandNewAdScrapes(scrapedApartments, activeNotEdited);


        List<ScrapedAd> translatedScrapedAds = scrapedAdsToSave
            .stream()
            .map(this::translateAd)
            .toList();

        for (var translatedScrapedAd : translatedScrapedAds) {
            if (translatedScrapedAd.getDescription() != null) {
                String summarizedDescription = gptService.summarizeDescription(translatedScrapedAd.getDescription());
                translatedScrapedAd.setDescription(summarizedDescription);
            }
        }

        for (var scrapedAd : translatedScrapedAds) {
            System.out.println(scrapedAd);
        }

        List<String> apartmentDescriptionsForEmbedding = translatedScrapedAds
            .stream()
            .map(ScrapedAd::toEmbeddableText)
            .toList();

        for (var apartmentDescription : apartmentDescriptionsForEmbedding) {
            System.out.println(apartmentDescription);
        }


        List<List<Float>> embeddings = apartmentDescriptionsForEmbedding
            .stream()
            .map(embeddingService::embedWithAda)
            .toList();

        adService.createAds(scrapedAdsToSave, embeddings, translatedScrapedAds, forSale);
        adService.saveAdsChanges();
    }



    private List<Ad> getActiveNotEdited(List<Ad> activeAds, List<Ad> editedAds) {
        List<Long> editedAdsId = editedAds.stream().map(Ad::getAdId).toList();
        return activeAds.stream()
            .filter(ad -> !editedAdsId.contains(ad.getAdId()))
            .toList();
    }

    private List<ScrapedAd> getBrandNewAdScrapes(List<ScrapedAd> scrapedAds, List<Ad> scrapedNotEdited) {
        List<String> scrapedNotEditedLinks = scrapedNotEdited.stream().map(Ad::getLink).toList();
        return scrapedAds.stream()
            .filter(scrapedAd -> !scrapedNotEditedLinks.contains(scrapedAd.getLink()))
            .toList();
    }


    private void printList(List<Ad> ads) {
        for (var ad : ads) {
            System.out.println(ad);
        }
    }

    private List<Ad> getActiveAds(List<Ad> ads, List<ScrapedAd> scrapedAds) {
        List<String> scrapedLinks = scrapedAds.stream().map(ScrapedAd::getLink).toList();
        return ads.stream()
            .filter(ad -> scrapedLinks.contains(ad.getLink()))
            .toList();
    }

    private List<Ad> getInactiveAds(List<Ad> ads, List<ScrapedAd> scrapedAds) {
        List<String> scrapedLinks = scrapedAds.stream().map(ScrapedAd::getLink).toList();
        return ads.stream()
            .filter(ad -> !scrapedLinks.contains(ad.getLink()))
            .toList();
    }

    //might be buggy
    private ScrapedAd translateAd(ScrapedAd scrapedAd) {
        return new ScrapedAd(
            translationService.translateToEnglish(scrapedAd.getTown()),
            scrapedAd.getNeighbourhood() != null ?
                translationService.translateToEnglish(scrapedAd.getNeighbourhood()) : null,
            scrapedAd.getDistrict() != null ?
                translationService.translateToEnglish(scrapedAd.getDistrict()) : null,
            scrapedAd.getAccommodationType(),
            scrapedAd.getPrice(),
            scrapedAd.getCurrency(),
            scrapedAd.getPropertyProvider()  != null ?
                translationService.translateToEnglish(scrapedAd.getPropertyProvider()) : null,
            scrapedAd.getSize(),
            scrapedAd.getFloor(),
            scrapedAd.getTotalFloors(),
            scrapedAd.isGasProvided(),
            scrapedAd.isThermalPowerPlantProvided(),
            scrapedAd.getPhoneNumber(),
            scrapedAd.getYearBuilt(),
            scrapedAd.getLink(),
            scrapedAd.getConstruction() != null ?
                translationService.translateToEnglish(scrapedAd.getConstruction()) : null,
            scrapedAd.getDescription() != null ?
                translationService.translateToEnglish(scrapedAd.getDescription()) : null,
            scrapedAd.getFeatures() != null ?
                translationService.translateToEnglish(scrapedAd.getFeatures()) : scrapedAd.getFeatures(),
            scrapedAd.getImageUrls()
        );
    }

    private List<Ad> getActiveButEdited(List<Ad> activeAds, List<ScrapedAd> scrapedAds, boolean forSale) {
        Map<String, Ad> persistedLinkToAds = new HashMap<>();
        Map<String, ScrapedAd> linkToScrapedAd = new HashMap<>();
        for (var ad : activeAds) {
            persistedLinkToAds.put(ad.getLink(), ad);
        }

        for (var scrapedAd : scrapedAds) {
            linkToScrapedAd.put(scrapedAd.getLink(), scrapedAd);
        }

        List<Ad> editedAds = new ArrayList<>();
        for (var persistedLinkToAd : persistedLinkToAds.entrySet()) {
            if (linkToScrapedAd.containsKey(persistedLinkToAd.getKey())) {
                ScrapedAd newAd = linkToScrapedAd.get(persistedLinkToAd.getKey());
                if (!isSameProperty(newAd, persistedLinkToAd.getValue(), forSale)) {

                    editedAds.add(persistedLinkToAd.getValue());
                }
            }
        }
        return editedAds;
    }

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
