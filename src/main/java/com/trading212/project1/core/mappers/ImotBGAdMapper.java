package com.trading212.project1.core.mappers;

import com.trading212.project1.core.exceptions.NotRentingAdException;
import com.trading212.project1.core.exceptions.ScrapeFormatMissMatchException;
import com.trading212.project1.core.models.scraping.ScrapedAd;
import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Currency;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImotBGAdMapper {
    private static final List<String> USELESS_FEATURES = new LinkedList<>();
    private static final String IMOTBG_BASE_URL = "https://www.imot.bg/";
    private static final Pattern UNIQUE_ID_REGEX_PATTERN = Pattern.compile("adv=(\\w+)");

    private ImotBGAdMapper() {
        throw new RuntimeException("should not be instantiated");
    }

    public static ScrapedAd fromDocument(Document document, String documentUrl) {
        ScrapedAd scrapedAd = new ScrapedAd();

        extractUniqueLink(scrapedAd, documentUrl);

        Element accommodationTypeElement = document.selectFirst(SelectorPath.ACCOMMODATION_TYPE_SELECTOR);
        if (accommodationTypeElement == null) {
            throw new ScrapeFormatMissMatchException("cannot extract accommodation type");
        }
        extractAccommodationType(scrapedAd, accommodationTypeElement);


        Element locationElement = document.selectFirst(SelectorPath.LOCATION_SELECTOR);
        if (locationElement == null) {
            throw new ScrapeFormatMissMatchException("cannot extract location");
        }
        extractLocation(scrapedAd, locationElement);

        Element priceElement = document.selectFirst(SelectorPath.PRICE_SELECTOR);
        if (priceElement == null) {
            priceElement = document.selectFirst(SelectorPath.PRICE_ALTERNATIVE_SELECTOR);
            if (priceElement == null) {
                throw new ScrapeFormatMissMatchException("cannot fetch price");
            }
        }
        extractPrice(scrapedAd, priceElement);

        Set<String> imageUrls = new HashSet<>();
        Element mainPictureElement = document.selectFirst(SelectorPath.MAIN_PICTURE_SELECTOR);
        if (mainPictureElement != null) {
            String mainPictureSrc = mainPictureElement.attr("src");
            if (!mainPictureSrc.isBlank() && !mainPictureSrc.isEmpty()) {
                imageUrls.add(mainPictureSrc.substring(2));
            }
        }

        Element restPicturesElement = document.selectFirst(SelectorPath.REST_PICTURES_SELECTOR);
        if (restPicturesElement != null) {
            Elements imgElements = restPicturesElement.select("img");
            for (Element img : imgElements) {
                String src = img.attr("src");
                if (!src.isBlank()) {
                    imageUrls.add(src.substring(2));
                }
            }
        }

        scrapedAd.setImageUrls(imageUrls.stream().toList());


        Element adParamElement = document.selectFirst(SelectorPath.AD_PARAM_SELECTOR);
        if (adParamElement != null) {
            extractAdParam(scrapedAd, adParamElement);
        }

        Element descriptionElement = document.selectFirst(SelectorPath.DESCRIPTION_SELECTOR);
        if (descriptionElement != null) {
            extractDescription(scrapedAd, descriptionElement);
        }

        scrapedAd.setFeatures(new ArrayList<>());
        Element featuresHeadingElement = document.selectFirst(SelectorPath.FEATURES_HEADING_SELECTOR);
        //for the features element to be present on the DOM there should be an element Особености
        if (featuresHeadingElement != null && featuresHeadingElement.text().startsWith("Особености")) {
            Element featuresElement = document.selectFirst((SelectorPath.FEATURES_SELECTOR));
            if (featuresElement != null) {
                extractFeatures(scrapedAd, featuresElement);
            }
        }

        Element phoneElement = document.selectFirst(SelectorPath.PHONE_NUMBER_SELECTOR);
        if (phoneElement != null) {
            extractPhoneNumber(scrapedAd, phoneElement);
        }

        Element propertyProviderElement = document.selectFirst(SelectorPath.PROVIDER_SELECTOR);
        if (propertyProviderElement == null) {
            propertyProviderElement = document.selectFirst(SelectorPath.ALTERNATIVE_PROVIDER_SELECTOR);
        }

        if (propertyProviderElement != null) {
            extractProvider(scrapedAd, propertyProviderElement);
        }


        return scrapedAd;
    }

    private static void extractUniqueLink(ScrapedAd scrapedAd, String url) {
        Matcher matcher = UNIQUE_ID_REGEX_PATTERN.matcher(url);

        if (matcher.find()) {
            String websiteId = matcher.group(1);
            scrapedAd.setLink(IMOTBG_BASE_URL + websiteId);
        } else {
            throw new ScrapeFormatMissMatchException("miss match with url ID");
        }
    }

    private static void extractProvider(ScrapedAd scrapedAd, Element providerElement) {
        String text = providerElement.text();
        scrapedAd.setPropertyProvider(text);
    }
    private static void extractFeatures(ScrapedAd scrapedAd, Element featuresElement) {
        Elements divElements = featuresElement.select("div");
        List<String> features = new ArrayList<>();
        for (Element div : divElements) {
            String feature = div.text().substring(1).trim();
            boolean skipFeature = false;
            for (var uselessFeature : USELESS_FEATURES) {
                if (uselessFeature.equals(feature)) {
                    skipFeature = true;
                }
            }
            if (skipFeature) {
                continue;
            }
            if (feature.equals("С действащ бизнес")) {
                throw new NotRentingAdException("business ad");
            }
            features.add(feature);
        }
        scrapedAd.setFeatures(features);
    }
    private static void extractPhoneNumber(ScrapedAd scrapedAd, Element phoneNumberElement) {
        String number = phoneNumberElement.text().trim();
        scrapedAd.setPhoneNumber(number);
    }
    private static void extractDescription(ScrapedAd scrapedAd, Element descriptionElement) {
        String description = descriptionElement.text().replace("<br>", System.lineSeparator())
                .replace("Виж по-малко...", "")
                    .replace("Виж повече", "");
        scrapedAd.setDescription(description);
    }

    private static void extractAdParam(ScrapedAd scrapedAd, Element adParamElement) {
        Elements divElements = adParamElement.children();
        for (Element div : divElements) {
            String text = div.ownText().trim();
            text = text.substring(0, text.length() - 1);
            Element strongElement = div.selectFirst("strong");
            String value = strongElement != null ? strongElement.ownText() : "";
            if (value.isEmpty()) {
                continue;
            }
            switch (text) {
                case "Площ":
                    value = value.split(" ")[0];
                    scrapedAd.setSize(Integer.parseInt(value));
                    break;
                case "Етаж":
                    value = value.trim();
                    String[] floorComponents = value.split(" ");
                    if (floorComponents.length < 3) {
                        throw new ScrapeFormatMissMatchException("error with floors");
                    }
                    if (floorComponents[0].startsWith("Партер")) {
                        scrapedAd.setFloor(1);
                    } else {
                        String[] floor = floorComponents[0].split("-");
                        scrapedAd.setFloor(Integer.parseInt(floor[0]));
                    }
                    scrapedAd.setTotalFloors(Integer.parseInt(floorComponents[floorComponents.length - 1]));
                    break;
                case "ТEЦ":
                    if (value.equals("ДА")) {
                        scrapedAd.setThermalPowerPlantProvided(true);
                    }
                    break;
                case "Газ":
                    if (value.equals("ДА")) {
                        scrapedAd.setGasProvided(true);
                    }
                    break;
                case "Строителство":
                    String[] constructionComponents = value.split(" ");
                    if (constructionComponents.length < 1) {
                        throw new ScrapeFormatMissMatchException("invalid scrape format");
                    }
                    String constructionType = constructionComponents[0].substring(0,
                        constructionComponents[0].length() - 1);
                    if (constructionType.equals("ЕПК")) {
                        constructionType = "едроплощен кофраж";
                    }
                    if (constructionType.equals("ПК")) {
                        constructionType = "пълзящ кофраж";
                    }
                    if (constructionComponents.length > 1) {
                        Integer yearBuilt = Integer.parseInt(constructionComponents[1]);
                        scrapedAd.setConstruction(constructionType);
                        scrapedAd.setYearBuilt(yearBuilt);
                    }
                    break;
                default:
                    throw new ScrapeFormatMissMatchException("unknown ad param" + text);
            }
        }
    }

    private static void extractLocation(ScrapedAd scrapedAd, Element locationElement) {
        String locationText = locationElement.text().trim();
        boolean inDistrict = false;
        if (locationText.contains("област")) {
            inDistrict = true;
        }
        String[] addressComponents = locationText.split(",");
        int length = addressComponents.length;
        if (length < 2) {
            throw new RuntimeException("no location information");
        }
        if (inDistrict) {
            String town = addressComponents[1].trim();
            town = replaceAcronymsInTown(town);
            scrapedAd.setTown(town);
            scrapedAd.setDistrict(addressComponents[0].trim());
        } else {
            scrapedAd.setTown(addressComponents[0].trim());
            scrapedAd.setNeighbourhood(addressComponents[1].trim());
        }
    }

    private static String replaceAcronymsInTown(String town) {
        return town.replace("с.", "село")
            .replace("к.к.", "курортен комплекс")
            .replace("м-т", "местност")
            .replace("в.з.", "вилна зона")
            .replace("яз.", "язовир")
            .replace("гр.", "град");
    }

    private static void extractAccommodationType(ScrapedAd scrapedAd, Element accommodationTypeElement) {
        String text = accommodationTypeElement.text();
        String[] accommodationComponents = text.split(" ");
        String accommodationTypeString = accommodationComponents[accommodationComponents.length - 1];
        switch (accommodationTypeString) {
            case "1-СТАЕН": scrapedAd.setAccommodationType(AccommodationType.ONE_ROOM);
            break;
            case "2-СТАЕН": scrapedAd.setAccommodationType(AccommodationType.TWO_ROOM);
            break;
            case "3-СТАЕН": scrapedAd.setAccommodationType(AccommodationType.THREE_ROOM);
            break;
            case "4-СТАЕН": scrapedAd.setAccommodationType(AccommodationType.FOUR_ROOM);
            break;
            case "МНОГОСТАЕН": scrapedAd.setAccommodationType(AccommodationType.MANY_ROOMS);
            break;
            case "МЕЗОНЕТ": scrapedAd.setAccommodationType( AccommodationType.MAISONETTE);
            break;
            case "ТАВАН" : scrapedAd.setAccommodationType(AccommodationType.STUDIO);
            break;
            case "СТАЯ" : scrapedAd.setAccommodationType(AccommodationType.ROOM);
            break;
            default: throw new ScrapeFormatMissMatchException("unknown accommodation type");
        }
    }

    private static void extractPrice(ScrapedAd scrapedAd, Element priceElement) {
        String priceText = priceElement.text().trim();
        if (priceText.contains("При запитване")) {
            throw new NotRentingAdException("no price for this ad");
        }
        String[] priceComponents = priceText.split(" ");
        String priceString = priceComponents[0];
        String currencyString = priceComponents[1];
        int length = priceComponents.length;
        if (length < 2) {
            throw new RuntimeException("no information about price");
        } else if (length == 4) {
            currencyString = priceComponents[3];
            priceString = priceComponents[0] + priceComponents[1] + priceComponents[2];
        } else if (length == 3) {
            currencyString = priceComponents[2];
            priceString = priceComponents[0] + priceComponents[1];
        }
        Integer price = Integer.parseInt(priceString);
        scrapedAd.setPrice(price);
        switch (currencyString) {
            case "лв.":
                scrapedAd.setCurrency(Currency.BGN);
                break;
            case "EUR":
                scrapedAd.setCurrency(Currency.EURO);
                break;
            case "$":
                scrapedAd.setCurrency(Currency.USD);
                break;
            default:
                throw new ScrapeFormatMissMatchException("unknown currency");
        }
    }



    static {
        USELESS_FEATURES.add("Възможност за дан. кредит");
        USELESS_FEATURES.add("Необзаведен");
        USELESS_FEATURES.add("Асансьор");
    }

    static class SelectorPath {
        private static final String LOCATION_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > div.advHeader > div.info > div.location";
        private static final String PRICE_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > div:nth-child(69) > table > tbody > tr > td:nth-child(2) > div:nth-child(1) > strong";
        private static final String PRICE_ALTERNATIVE_SELECTOR = "#cena";
        private static final String MAIN_PICTURE_SELECTOR = "#bigPictureCarousel";
        private static final String REST_PICTURES_SELECTOR = "#pictures_moving_details_small";
        private static final String AD_PARAM_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > div.adParams";
        private static final String DESCRIPTION_SELECTOR = "#description_div";
        private static final String FEATURES_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > table:nth-child(86) > tbody > tr";
        private static final String FEATURES_HEADING_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > div:nth-child(85)";
        private static final String PHONE_NUMBER_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(2) > div:nth-child(1) > div > div > div.phone";
        private static final String PROVIDER_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > table:nth-child(88) > tbody > tr > td:nth-child(2) > b";
        private static final String ALTERNATIVE_PROVIDER_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > table:nth-child(88) > tbody > tr > td:nth-child(2) > div > b";
        private static final String ACCOMMODATION_TYPE_SELECTOR = "body > div:nth-child(2) > table > tbody > tr:nth-child(1) > td:nth-child(1) > form > div:nth-child(69) > table > tbody > tr > td:nth-child(1) > div:nth-child(1) > h1";
    }

   
}
