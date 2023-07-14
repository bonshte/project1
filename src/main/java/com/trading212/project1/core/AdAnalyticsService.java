package com.trading212.project1.core;


import com.trading212.project1.core.models.AdStub;
import com.trading212.project1.repositories.analytics.AdAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class AdAnalyticsService {

    private AdAnalyticsRepository adAnalyticsRepository;
    private ConcurrentHashMap<AdStub, Integer> adToViews;
    private CountDownLatch latch;
    private ReadWriteLock readWriteLock;


    public AdAnalyticsService(AdAnalyticsRepository adAnalyticsRepository) {
        this.adAnalyticsRepository = adAnalyticsRepository;
        adToViews = new ConcurrentHashMap<>();
        latch = new CountDownLatch(0);
        readWriteLock = new ReentrantReadWriteLock();
    }

    public void updateAd(AdStub adStub) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        readWriteLock.readLock().lock();
        adToViews.compute(adStub, (key, value) -> (value == null) ? 1 : value + 1);

        readWriteLock.readLock().unlock();
    }

    public void writeToAnalyticalRepository() {
        latch = new CountDownLatch(1);
        readWriteLock.writeLock().lock();

        var entrySet = adToViews.entrySet();
        for (var entry : entrySet) {
            var currentAd = entry.getKey();
            adAnalyticsRepository.createAdView(currentAd.getId(), currentAd.getCountry(), currentAd.getSubLocal(),
                currentAd.getNeighbourhood(), currentAd.getPropertyType(), currentAd.getPrice(), entry.getValue());
        }

        adToViews.clear();
        readWriteLock.writeLock().unlock();
        latch.countDown();
    }

}
