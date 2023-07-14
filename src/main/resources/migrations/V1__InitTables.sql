CREATE TABLE User (
user_id INT AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(255) UNIQUE NOT NULL,
phone_number VARCHAR(255) UNIQUE NOT NULL,
email VARCHAR(255) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL,
created_at DATE NOT NULL,
user_rating DECIMAL(3,2) NOT NULL,
CHECK (user_rating >= 0 AND user_rating <= 5),
role ENUM("ADMIN", "USER")
);

CREATE TABLE Landlord (
user_id INT PRIMARY KEY,
landlord_rating DECIMAL(3,2) NOT NULL,
CHECK (landlord_rating >=0 AND landlord_rating <= 5),
FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE Client_Requirement (
id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT NOT NULL REFERENCES User(user_id),
price_per_day_min INT,
price_per_day_max INT,
country VARCHAR(511) NOT NULL,
sublocally VARCHAR(511) NOT NULL,
neighbourhood VARCHAR(511),
street VARCHAR(511),
currency ENUM('BGN',
'USD',
'EURO') NOT NULL,
region VARCHAR(255) NOT NULL,
bedroom_count TINYINT,
bathroom_count TINYINT,
property_area FLOAT,
property_type ENUM('ONE_ROOM',
'TWO_ROOM',
'THREE_ROOM',
'FOUR_ROOM',
'MULTI_ROOM',
'MAISONETTE',
'STUDIO',
'HOUSE',
'VILLA'),
check_in_date DATE NOT NULL,
check_out_date DATE NOT NULL,
created_on DATETIME NOT NULL,
CHECK (check_out_date > check_in_date),
CHECK (property_area > 0),
CHECK (bathroom_count > 0),
CHECK (bedroom_count > 0),
CHECK (price_per_day_min > 0),
CHECK (price_per_day_max >= price_per_day_min)
);

CREATE TABLE Property (
    property_id INT AUTO_INCREMENT PRIMARY KEY,
    country VARCHAR(511) NOT NULL,
    sublocally VARCHAR(511) NOT NULL,
    neighbourhood VARCHAR(511),
    street VARCHAR(511),
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,9) NOT NULL,
    property_area FLOAT NOT NULL,
    property_type ENUM('ONE ROOM',
'TWO ROOM',
'THREE ROOM',
'FOUR ROOM',
'MULTI_ROOM',
'MAISONETTE',
'STUDIO',
'HOUSE',
'VILLA') NOT NULL,
    owner_id INT REFERENCES User(user_id),
    build_date DATE,
    bedroom_count TINYINT NOT NULL,
    bathroom_count TINYINT NOT NULL,
    property_rating DECIMAL(3,2) NOT NULL,
    CHECK (property_area > 0),
    CHECK (bedroom_count > 0),
    CHECK (bathroom_count > 0)
);

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

CREATE TABLE Ad_Image(
ad_id INT REFERENCES Ad(ad_id),
image_url VARCHAR(255),
PRIMARY KEY(ad_id, image_url)
);

CREATE TABLE Review (
review_id INT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE Property_Review (
review_id INT PRIMARY KEY REFERENCES Review(review_id),
property_id INT REFERENCES Property(property_id),
reviewer_id INT REFERENCES User(user_id),
time_of_review DATETIME,
description TEXT,
rating DECIMAL(3,2) CHECK(rating >= 0 AND rating <= 5)
);

CREATE TABLE Customer_Review (
review_id INT PRIMARY KEY REFERENCES Review(review_id),
reviewer_id INT REFERENCES User(user_id),
reviewed_id INT REFERENCES User(user_id),
time_of_review DATETIME,
description TEXT,
rating DECIMAL(3,2) CHECK(rating >= 0 AND rating <= 5)
);

CREATE TABLE Landlord_Review (
review_id INT PRIMARY KEY REFERENCES Review(review_id),
reviewer_id INT REFERENCES User(user_id),
reviewed_id INT REFERENCES User(user_id),
time_of_review DATETIME,
description TEXT,
rating DECIMAL(3,2) CHECK(rating >= 0 AND rating <= 5)
);

CREATE TABLE Bookings (
booking_id INT AUTO_INCREMENT PRIMARY KEY,
tenant_id INT NOT NULL,
landlord_id INT NOT NULL,
country VARCHAR(511) NOT NULL,
sublocally VARCHAR(511) NOT NULL,
neighbourhood VARCHAR(511),
street VARCHAR(511),
ad_id INT NOT NULL,
check_in_date DATE NOT NULL,
check_out_date DATE NOT NULL,
FOREIGN KEY (tenant_id) REFERENCES User(user_id),
FOREIGN KEY (landlord_id) REFERENCES User(user_id),
FOREIGN KEY (ad_id) REFERENCES Ad(ad_id)
);

CREATE TABLE Watch_List (
user_id INT REFERENCES User(user_id),
property_id INT REFERENCES Property(property_id),
PRIMARY KEY (user_id, property_id)
);

CREATE TABLE Review_Images (
review_id INT,
image_url VARCHAR(255),
PRIMARY KEY (review_id, image_url),
FOREIGN KEY (review_id) REFERENCES Review(review_id)
);

CREATE TABLE Property_Images (
property_id INT,
image_url VARCHAR(255),
PRIMARY KEY (property_id, image_url),
FOREIGN KEY (property_id) REFERENCES Property(property_id)
);