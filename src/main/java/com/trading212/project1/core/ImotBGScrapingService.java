package com.trading212.project1.core;

import com.trading212.project1.core.exceptions.NotRentingAdException;
import com.trading212.project1.core.exceptions.ScrapeFormatMissMatchException;
import com.trading212.project1.core.exceptions.SearchFilterScrapeException;
import com.trading212.project1.core.models.scraping.ScrapedAd;
import com.trading212.project1.core.models.scraping.ScrapeSearchFilter;
import com.trading212.project1.core.mappers.ImotBGAdMapper;
import com.trading212.project1.core.models.scraping.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ImotBGScrapingService {


    private static final ScrapeConfig RENT_SCRAPE_CONFIG = new ScrapeConfig(
        ImotBGUtils.RENT_SCRAPE_URL,
                ImotBGUtils::getIdForRentAccommodation,
                ImotBGAdMapper::fromDocument,
        ImotBGUtils.ALL_RENT_SEARCH_FILTERS
        );

    private static final ScrapeConfig PURCHASE_SCRAPE_CONFIG = new ScrapeConfig(
        ImotBGUtils.PURCHASE_SCRAPE_URL,
        ImotBGUtils::getIdForPurchaseAccommodation,
        ImotBGAdMapper::fromDocument,
        ImotBGUtils.ALL_PURCHASE_SEARCH_FILTERS
    );


    private static final String COOKIES_ACCEPT_BUTTON_SELECTOR = ".fc-button.fc-cta-consent.fc-primary-button";
    private static final String SEARCH_BUTTON_SELECTOR = "input[type='button'][value='Т Ъ Р С И']";
    private static final String RESULT_PAGES_COUNT_SELECTOR = "span.pageNumbersInfo";
    private static final String REGION_CHOICE_SCRIPT = "var clickEvent = document.createEvent ('MouseEvents');clickEvent.initEvent ('click', true, true);arguments[0].dispatchEvent (clickEvent);";
    private static final Duration DEFAULT_DRIVER_TIMEOUT = Duration.ofSeconds(30);
    private static final int PAGE_TIMEOUT = 5000;
    private static final int TIME_TO_LOAD_JS = 2000;
    public static void main(String[] args) {
        scrape(PURCHASE_SCRAPE_CONFIG);
    }
    public static ScrapingResult scrape(ScrapeConfig scrapeConfig) {
        WebDriver driver = new SafariDriver();
        driver.get(scrapeConfig.getScrapeUrl());
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_DRIVER_TIMEOUT);

        WebElement popupButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector(COOKIES_ACCEPT_BUTTON_SELECTOR)));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", popupButton);

        List<ScrapedAd> totalScrapedAds = new LinkedList<>();
        List<ScrapeSearchFilter> failedFilters = new LinkedList<>();

        for (var filter : scrapeConfig.getFilters()) {
            driver.get(scrapeConfig.getScrapeUrl());
            // After the popup is closed, find the SVG element by its id and simulate click on it
            new WebDriverWait(driver, DEFAULT_DRIVER_TIMEOUT)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    String svgElementId = ImotBGUtils.getIdForLocation(filter.getLocation());
                    WebElement svgElement = d.findElement(By.id(svgElementId));
                    ((JavascriptExecutor) d).executeScript(REGION_CHOICE_SCRIPT, svgElement);
                    return true;
                });

            boolean isAccommodationTypeSelected = false;
            while (!isAccommodationTypeSelected) {
                sleep(TIME_TO_LOAD_JS);
                var accommodationCodeGenerator = scrapeConfig.getAccommodationTypeCodeGenerator();
                String checkboxElementId = accommodationCodeGenerator.apply(filter.getAccommodationType());
                new WebDriverWait(driver, DEFAULT_DRIVER_TIMEOUT)
                    .ignoring(StaleElementReferenceException.class)
                    .until((WebDriver d) -> {

                        WebElement checkbox = d.findElement(By.id(checkboxElementId));
                        ((JavascriptExecutor) d).executeScript("arguments[0].click();", checkbox);
                        return true;
                    });
                WebElement checkBox = driver.findElement(By.id(checkboxElementId));
                isAccommodationTypeSelected = checkBox.isSelected();
            }

            new WebDriverWait(driver, DEFAULT_DRIVER_TIMEOUT)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    WebElement searchButton = d.findElement(By.cssSelector(SEARCH_BUTTON_SELECTOR));
                    ((JavascriptExecutor) d).executeScript("arguments[0].click();", searchButton);
                    return true;
                });

            sleep(TIME_TO_LOAD_JS);
            String currentUrl = driver.getCurrentUrl();
            try {
                List<ScrapedAd> scrappedResults = scrapeResultPages(currentUrl, scrapeConfig);
                totalScrapedAds.addAll(scrappedResults);
            } catch (SearchFilterScrapeException e) {
                failedFilters.add(filter);
            }
        }
        driver.quit();
        return new ScrapingResult(totalScrapedAds, failedFilters);
    }

    private static List<ScrapedAd> scrapeResultPages(
        String firstPageUrl,
        ScrapeConfig scrapeConfig) {
        int pages = 1;
        try {
            Document doc = Jsoup.connect(firstPageUrl).get();
            Element pageNumbersInfoElement = doc.selectFirst(RESULT_PAGES_COUNT_SELECTOR);
            if (pageNumbersInfoElement == null) {
                throw new SearchFilterScrapeException("could not scrape filter for "  + firstPageUrl);
            }
            String pageNumberInfo = pageNumbersInfoElement.text();
            String[] partitionedText = pageNumberInfo.split(" ");
            pages = Integer.parseInt(partitionedText[partitionedText.length - 1]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SearchFilterScrapeException("could not scrape filter for "  + firstPageUrl);
        }
        List<ScrapedAd> scrapedAds = new LinkedList<>();
        String baseFilterSearchUrl = firstPageUrl.substring(0, firstPageUrl.length() - 1);

        for (int i = 1; i <= pages; ++i) {
            String currentUrl = baseFilterSearchUrl + i;
            scrapedAds.addAll(scrapeResultPage(currentUrl, scrapeConfig));
            sleep(PAGE_TIMEOUT);
        }
        return scrapedAds;
    }
    private static List<ScrapedAd> scrapeResultPage(
        String url,
        ScrapeConfig scrapeConfig) {
        List<ScrapedAd> adsScraped = new LinkedList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            var allTables = doc.select("table");
            List<Element> adTables = new ArrayList<>();
            for (var table : allTables) {
                String style = table.attr("style");
                if (style.contains("margin-bottom:0px")) {
                    adTables.add(table);
                }
            }

            for (Element adTable : adTables) {

                Element linkElement = adTable.selectFirst("a.lnk1");
                if (linkElement == null) {
                    //log a link was not found for an ad and continue
                } else {
                    try {
                        String adLink = linkElement.attr("href").substring(2);
                        adsScraped.add(scrapeAdPage("https://" + adLink,  scrapeConfig));
                    } catch (NotRentingAdException er) {
                        //this was a bad ad, we skip it
                    } catch (ScrapeFormatMissMatchException e) {
                        //miss match with the scrape format for this add
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            //if some page does not fully scrape we will ignore the missed and continue
            e.printStackTrace();
        }
        return adsScraped;
    }
    private static ScrapedAd scrapeAdPage(
        String link,
        ScrapeConfig scrapeConfig) {
        try {
            Document document = Jsoup.connect(link).get();
            return scrapeConfig.getAdExtractor().apply(document, link);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted", e);
        }
    }
}
