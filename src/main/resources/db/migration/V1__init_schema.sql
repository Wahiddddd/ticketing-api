-- V1: Initial Schema Setup

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20),
    balance DOUBLE DEFAULT 0.0,
    failed_attempt INT DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE
);

CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    event_date DATETIME,
    status VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    admin_id VARCHAR(36),
    CONSTRAINT fk_event_admin FOREIGN KEY (admin_id) REFERENCES users(id)
);

CREATE TABLE event_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT,
    category_name VARCHAR(255),
    category_code VARCHAR(50),
    price DOUBLE,
    total_capacity INT,
    remaining_capacity INT,
    CONSTRAINT fk_category_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE (event_id, category_code)
);

CREATE TABLE tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_category_id BIGINT,
    ticket_code VARCHAR(100) UNIQUE,
    seat_number VARCHAR(50),
    status VARCHAR(20),
    user_id VARCHAR(36),
    CONSTRAINT fk_ticket_category FOREIGN KEY (event_category_id) REFERENCES event_categories(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36),
    ticket_id BIGINT,
    amount_paid DOUBLE,
    transaction_type VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_transaction_ticket FOREIGN KEY (ticket_id) REFERENCES tickets(id)
);
