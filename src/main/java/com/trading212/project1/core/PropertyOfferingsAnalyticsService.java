package com.trading212.project1.core;

import com.trading212.project1.core.models.AdStub;
import com.trading212.project1.repositories.analytics.PropertyOfferingsAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class PropertyOfferingsAnalyticsService {

    private PropertyOfferingsAnalyticsRepository propertyOfferingsAnalyticsRepository;
    private ConcurrentLinkedQueue<AdStub> offeredProperties;
    private CountDownLatch latch;
    private ReadWriteLock readWriteLock;


    public PropertyOfferingsAnalyticsService(PropertyOfferingsAnalyticsRepository propertyOfferingsAnalyticsRepository) {
        this.propertyOfferingsAnalyticsRepository = propertyOfferingsAnalyticsRepository;
        offeredProperties = new ConcurrentLinkedQueue<>();
        latch = new CountDownLatch(0);
        readWriteLock = new ReentrantReadWriteLock();
    }

    public void addPropertyOffering(AdStub adStub) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        readWriteLock.readLock().lock();

        offeredProperties.add(adStub);

        readWriteLock.readLock().unlock();
    }

    public void writeToAnalyticalRepository() {
        latch = new CountDownLatch(1);
        readWriteLock.writeLock().lock();

        for (var offeredProperty : offeredProperties) {
            propertyOfferingsAnalyticsRepository.createPropertyOffering(
                offeredProperty.getCountry(),
                offeredProperty.getSubLocal(),
                offeredProperty.getNeighbourhood(),
                offeredProperty.getPropertyArea(),
                offeredProperty.getPropertyType(),
                offeredProperty.getPrice()
            );
        }

        offeredProperties.clear();
        readWriteLock.writeLock().unlock();
        latch.countDown();
    }

}


