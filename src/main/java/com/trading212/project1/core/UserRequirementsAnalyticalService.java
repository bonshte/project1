package com.trading212.project1.core;

import com.trading212.project1.core.models.UserRequirementStub;
import com.trading212.project1.repositories.analytics.UserRequirementsAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class UserRequirementsAnalyticalService {

    private UserRequirementsAnalyticsRepository userRequirementsAnalyticsRepository;
    private ConcurrentLinkedQueue<UserRequirementStub> userRequirements;
    private CountDownLatch latch;
    private ReadWriteLock readWriteLock;


    public UserRequirementsAnalyticalService(UserRequirementsAnalyticsRepository userRequirementsAnalyticsRepository) {
        this.userRequirementsAnalyticsRepository = userRequirementsAnalyticsRepository;
        userRequirements = new ConcurrentLinkedQueue<>();
        latch = new CountDownLatch(0);
        readWriteLock = new ReentrantReadWriteLock();
    }

    public void addUserRequirement(UserRequirementStub userRequirementStub) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

        readWriteLock.readLock().lock();
        userRequirements.add(userRequirementStub);
        readWriteLock.readLock().unlock();
    }

    public void writeToAnalyticalRepository() {
        latch = new CountDownLatch(1);
        readWriteLock.writeLock().lock();

        for (var userRequirement : userRequirements) {
            userRequirementsAnalyticsRepository.createUserRequirement(
                userRequirement.getCountry(),
                userRequirement.getSubLocal(),
                userRequirement.getNeighbourhood(),
                userRequirement.getPropertyType(),
                userRequirement.getPrice()
            );
        }

        userRequirements.clear();
        readWriteLock.writeLock().unlock();
        latch.countDown();
    }
}
