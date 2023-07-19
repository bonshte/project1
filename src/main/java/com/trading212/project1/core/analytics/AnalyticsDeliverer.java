package com.trading212.project1.core.analytics;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsDeliverer {

    private PropertyOfferingsAnalyticsService propertyOfferingsAnalyticsService;
    private UserRequirementsAnalyticalService userRequirementsAnalyticalService;
    private AdAnalyticsService adAnalyticsService;

    public AnalyticsDeliverer(PropertyOfferingsAnalyticsService propertyOfferingsAnalyticsService,
                              UserRequirementsAnalyticalService userRequirementsAnalyticalService,
                              AdAnalyticsService adAnalyticsService) {
        this.adAnalyticsService = adAnalyticsService;
        this.propertyOfferingsAnalyticsService = propertyOfferingsAnalyticsService;
        this.userRequirementsAnalyticalService = userRequirementsAnalyticalService;
    }

    @Scheduled(cron = "*/5 * * * *")
    public void updateAdViewsAnalytics() {
        adAnalyticsService.writeToAnalyticalRepository();
    }

    @Scheduled(cron = "0 */12 * * *")
    public void updateUserRequirementsAnalytics() {
        userRequirementsAnalyticalService.writeToAnalyticalRepository();
    }

    @Scheduled(cron = "0 */12 * * *")
    public void updatePropertyOfferingsAnalytics() {
        propertyOfferingsAnalyticsService.writeToAnalyticalRepository();
    }
}
