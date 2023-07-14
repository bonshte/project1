package com.trading212.project1.repositories.entities;

import com.trading212.project1.core.models.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyEntity {
    private int id;
    private String country;
    private String subLocal;
    private String neighbourhood;
    private double latitude;
    private double longitude;
    private float propertyArea;
    private PropertyType propertyType;
    private int ownerID;
    private LocalDate buildDate;
    private int bedroomCount;
    private int bathroomCount;
    private float propertyRating;

}
