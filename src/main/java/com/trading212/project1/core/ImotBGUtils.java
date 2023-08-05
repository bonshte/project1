package com.trading212.project1.core;

import com.trading212.project1.core.models.scraping.ScrapeSearchFilter;
import com.trading212.project1.core.models.scraping.AccommodationType;
import com.trading212.project1.core.models.scraping.Location;

import java.util.ArrayList;
import java.util.List;

public final class ImotBGUtils {
    public static final String RENT_SCRAPE_URL = "https://www.imot.bg/pcgi/imot.cgi?act=2&rub=2";
    public static final String PURCHASE_SCRAPE_URL = "https://www.imot.bg/pcgi/imot.cgi?act=2&rub=1";


    public static final List<ScrapeSearchFilter> ALL_RENT_SEARCH_FILTERS = new ArrayList<>();
    public static final List<ScrapeSearchFilter> ALL_PURCHASE_SEARCH_FILTERS = new ArrayList<>();

    public static final List<ScrapeSearchFilter> BURGAS_PURCHASE_SEARCH_FILTERS = new ArrayList<>();
    public static final List<ScrapeSearchFilter> BURGAS_RENT_SEARCH_FILTERS = new ArrayList<>();

    private ImotBGUtils() {
        throw new AssertionError("Cannot instantiate " + this.getClass().getName());
    }

    public static String getIdForLocation(Location location) {
        return switch (location) {
            case SOFIA -> "BG-23";
            case VIDIN -> "city_5";
            case VIDIN_DISTRICT -> "BG-05";
            case MONTANA -> "city_12";
            case MONTANA_DISTRICT -> "BG-12";
            case VRATSA -> "city_6";
            case VRATSA_DISTRICT -> "BG-06";
            case PLEVEN -> "city_15";
            case PLEVEN_DISTRICT -> "BG-15";
            case RUSE -> "city_18";
            case RUSE_DISTRICT -> "BG-18";
            case SILISTRA -> "city_19";
            case SILISTRA_DISTRICT -> "BG-19";
            case DOBRICH -> "city_8";
            case DOBRICH_DISTRICT -> "BG-08";
            case VARNA -> "city_3";
            case VARNA_DISTRICT -> "BG-03";
            case SHUMEN -> "city_27";
            case SHUMEN_DISTRICT -> "BG-27";
            case RAZGRAD -> "city_17";
            case RAZGRAD_DISTRICT -> "BG-17";
            case TARGOVISHTE -> "city_25";
            case TARGOVISHTE_DISTRICT -> "BG-25";
            case BURGAS -> "city_2";
            case BURGAS_DISTRICT -> "BG-02";
            case SLIVEN -> "city_20";
            case SLIVEN_DISTRICT -> "BG-20";
            case YAMBOL -> "city_28";
            case YAMBOL_DISTRICT -> "BG-28";
            case VELIKO_TARNOVO -> "city_4";
            case VELIKO_TARNOVO_DISTRICT -> "BG-04";
            case GABROVO -> "city_7";
            case GABROVO_DISTRICT -> "BG-07";
            case STARA_ZAGORA -> "city_24";
            case STARA_ZAGORA_DISTRICT -> "BG-24";
            case HASKOVO -> "city_26";
            case HASKOVO_DISTRICT -> "BG-26";
            case KARDZHALI -> "city_9";
            case KARDZHALI_DISTRICT -> "BG-09";
            case SMOLYAN -> "city_21";
            case SMOLYAN_DISTRICT -> "BG-21";
            case PLOVDIV -> "city_16";
            case PLOVDIV_DISTRICT -> "BG-16";
            case LOVECH -> "city_11";
            case LOVECH_DISTRICT -> "BG-11";
            case BLAGOEVGRAD -> "city_1";
            case BLAGOEVGRAD_DISTRICT -> "BG-01";
            case KYUSTENDIL -> "city_10";
            case KYUSTENDIL_DISTRICT -> "BG-10";
            case PERNIK -> "city_14";
            case PERNIK_DISTRICT -> "BG-14";
            case PAZARDZHIK -> "city_13";
            case PAZARDZHIK_DISTRICT -> "BG-13";
            case SOFIA_DISTRICT -> "BG-22";
        };
    }

    public static String getIdForRentAccommodation(AccommodationType accommodationType) {
        return switch (accommodationType) {
            case ROOM -> "vi1";
            case ONE_ROOM -> "vi2";
            case TWO_ROOM -> "vi3";
            case THREE_ROOM -> "vi4";
            case FOUR_ROOM -> "vi5";
            case STUDIO -> "vi9";
            case MANY_ROOMS -> "vi6";
            case MAISONETTE -> "vi7";
            default -> throw new RuntimeException("Error with accommodation reference");
        };
    }

    public static String getIdForPurchaseAccommodation(AccommodationType accommodationType) {
        return switch (accommodationType) {
            case ROOM -> "vi0";
            case ONE_ROOM -> "vi1";
            case TWO_ROOM -> "vi2";
            case THREE_ROOM -> "vi3";
            case FOUR_ROOM -> "vi4";
            case STUDIO -> "vi8";
            case MANY_ROOMS -> "vi5";
            case MAISONETTE -> "vi6";
            default -> throw new RuntimeException("Error with accommodation reference");
        };
    }

    static {
        for (var location : Location.values()) {
            for (var accommodationType : AccommodationType.values()) {
                ALL_RENT_SEARCH_FILTERS.add(new ScrapeSearchFilter(location, accommodationType));
                if (accommodationType != AccommodationType.ROOM) {
                    ALL_PURCHASE_SEARCH_FILTERS.add(new ScrapeSearchFilter(location, accommodationType));
                }
            }
        }


        BURGAS_RENT_SEARCH_FILTERS.add(new ScrapeSearchFilter(Location.BURGAS_DISTRICT, AccommodationType.ONE_ROOM));


        BURGAS_PURCHASE_SEARCH_FILTERS.add(new ScrapeSearchFilter(Location.BURGAS_DISTRICT, AccommodationType.ONE_ROOM));



    }
}