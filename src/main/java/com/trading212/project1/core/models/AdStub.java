package com.trading212.project1.core.models;

public class AdStub {
    public int getId() {
        return 0;
    }

    public String getCountry() {
        return null;
    }

    public String getNeighbourhood() {
        return null;
    }

    public String getSubLocal() {
        return null;
    }

    public PropertyType getPropertyType() {
        return null;
    }

    public int getPrice() {
        return 0;
    }

    public int getPropertyArea() {
        return 0;
    }



/*
CREATE TABLE Ad (
ad_id INT AUTO_INCREMENT PRIMARY KEY,
landlord_id INT NOT NULL,
property_id INT NOT NULL,
posted_on DATETIME NOT NULL,
price_per_day INT NOT NULL,
country VARCHAR(511) NOT NULL,
sublocally VARCHAR(511) NOT NULL,
neighbourhood VARCHAR(511),
street VARCHAR(511),
currency ENUM('BGN',
'USD',
'EURO') NOT NULL,
description TEXT,
times_visited INT DEFAULT 0,
phone_number VARCHAR(255) NOT NULL,
min_rating_book DECIMAL(3,2),
premium BOOLEAN DEFAULT FALSE,
active BOOLEAN DEFAULT TRUE,
CHECK (price_per_day > 0),
FOREIGN KEY (landlord_id) REFERENCES User(user_id),
FOREIGN KEY (property_id) REFERENCES Property(property_id)
);
 */
}
