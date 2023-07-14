package com.trading212.project1.repositories;

import com.trading212.project1.core.models.PropertyType;
import com.trading212.project1.repositories.entities.PropertyEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PropertyRepository {

    Optional<PropertyEntity> getPropertyById(int id);

    List<PropertyEntity> getProperties();

    PropertyEntity createProperty(String country, String subLocal, String neighbourhood,
                                  double latitude, double longitude, float propertyArea,
                                  PropertyType propertyType, int ownerID, LocalDate buildDate,
                                  int bedroomCount, int bathroomCount, float propertyRating);

    int deletePropertyById(int id);

}
