CREATE TABLE user (
user_id INT AUTO_INCREMENT PRIMARY KEY,
email VARCHAR(255) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL,
premium_until DATE,
criteria TEXT,
is_subscribed BOOLEAN,
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

