CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    premium_until DATE,
    role ENUM("ADMIN","USER")
);

CREATE TABLE chatSession (
    chat_session_id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) DEFAULT 'New Chat',
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE chatMessage (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    chat_session_id INT NOT NULL,
    sent_message TEXT NOT NULL,
    translated_message TEXT NOT NULL,
    from_user BOOLEAN NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_session_id) REFERENCES chatSession(chat_session_id)
);

CREATE TABLE ads (
    ad_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    for_sale BOOLEAN NOT NULL,
    town VARCHAR(255) NOT NULL,
    neighbourhood VARCHAR(255),
    district VARCHAR(255),
    accommodation_type ENUM('ROOM','ONE_ROOM','TWO_ROOM','THREE_ROOM','FOUR_ROOM', 'MANY_ROOMS', 'MAISONETTE', 'STUDIO') NOT NULL,
    price INT NOT NULL,
    currency ENUM('USD', 'EURO', 'BGN') NOT NULL,
    property_provider VARCHAR(255),
    size INT,
    floor INT,
    total_floors INT,
    gas_provided BOOLEAN,
    thermal_power_plant_provided BOOLEAN,
    phone_number VARCHAR(20),
    year_built INT,
    link VARCHAR(255) NOT NULL UNIQUE,
    construction VARCHAR(255),
    description TEXT
);


CREATE TABLE adFeature (
    ad_id BIGINT,
    feature VARCHAR(255),
    PRIMARY KEY (ad_id, feature),
    FOREIGN KEY (ad_id) REFERENCES ads(ad_id) ON DELETE CASCADE
);

CREATE TABLE adImageUrl (
    ad_id BIGINT,
    image_url VARCHAR(255),
    PRIMARY KEY (ad_id, image_url),
    FOREIGN KEY (ad_id) REFERENCES ads(ad_id) ON DELETE CASCADE
);


CREATE TABLE chatSessionRecommendation (
    chat_session_id INT,
    ad_id BIGINT,
    user_id INT,
    recommended_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    for_sale BOOLEAN NOT NULL,
    PRIMARY KEY(chat_session_id, ad_id),
    FOREIGN KEY(user_id) REFERENCES user(user_id),
    FOREIGN KEY(chat_session_id) REFERENCES chatSession(chat_session_id),
    FOREIGN KEY(ad_id) REFERENCES ads(ad_id) ON DELETE CASCADE
);

