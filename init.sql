-- ============================================================================
-- Parking Slot Microservices - Initial Database Schema & Seed Data
-- Automatically executed on first container startup via /docker-entrypoint-initdb.d/
-- ============================================================================

-- 1. Sequences for Hibernate ID generators
CREATE SEQUENCE IF NOT EXISTS availability_seq START WITH 100 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS building_seq START WITH 100 INCREMENT BY 50;

-- 2. Hibernate Sequence Table (used by TableGenerator strategy)
CREATE TABLE IF NOT EXISTS hibernate_sequences (
    sequence_name VARCHAR(255) NOT NULL PRIMARY KEY,
    next_val BIGINT
);

INSERT INTO hibernate_sequences (sequence_name, next_val)
VALUES ('default', 100)
ON CONFLICT (sequence_name) DO UPDATE SET next_val = 100;

-- 3. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER NOT NULL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(255),
    city VARCHAR(255)
);

-- 4. Building Table (References users.id via user_id)
CREATE TABLE IF NOT EXISTS building (
    id INTEGER NOT NULL PRIMARY KEY,
    building_number VARCHAR(255),
    building_name VARCHAR(255),
    area VARCHAR(255),
    town VARCHAR(255),
    state VARCHAR(255),
    landmark VARCHAR(255),
    pincode INTEGER,
    user_id INTEGER REFERENCES users(id)
);

-- 5. Availability Table (Time windows)
CREATE TABLE IF NOT EXISTS availability (
    id INTEGER NOT NULL PRIMARY KEY,
    from_date TIMESTAMP(6) WITHOUT TIME ZONE,
    to_date TIMESTAMP(6) WITHOUT TIME ZONE
);

-- 6. Slot Table (References building.id and availability.id)
CREATE TABLE IF NOT EXISTS slot (
    id INTEGER NOT NULL PRIMARY KEY,
    slot_number VARCHAR(255),
    floornumber VARCHAR(255),
    division_no VARCHAR(255),
    building_id INTEGER REFERENCES building(id),
    availability_id INTEGER UNIQUE REFERENCES availability(id)
);

-- 7. Slot Booking Table (References slot.id, users.id, and availability.id)
CREATE TABLE IF NOT EXISTS slot_booking (
    id INTEGER NOT NULL PRIMARY KEY,
    booking_date DATE,
    status VARCHAR(255),
    slot_id INTEGER REFERENCES slot(id),
    user_id INTEGER REFERENCES users(id),
    availabilty_id INTEGER REFERENCES availability(id)
);

-- ============================================================================
-- Sample Seed Data (3 Records per Table)
-- ============================================================================

-- Users: Admin and regular users
INSERT INTO users (id, name, email, password, role, city) VALUES
(1, 'Admin User', 'admin@parking.com', 'admin123', 'ADMIN', 'Hyderabad'),
(2, 'Venkatesh Beeraka', 'venkateshbeeraka5@gmail.com', 'user123', 'USER', 'Bangalore'),
(3, 'Alice Smith', 'alice.smith@example.com', 'user123', 'USER', 'Mumbai')
ON CONFLICT (id) DO NOTHING;

-- Buildings
INSERT INTO building (id, building_number, building_name, area, town, state, landmark, pincode, user_id) VALUES
(1, 'BLD-101', 'Cyber Towers', 'Hitec City', 'Hyderabad', 'Telangana', 'Near Cyber Gateway', 500081, 1),
(2, 'BLD-202', 'Prestige Tech Park', 'Marathahalli', 'Bangalore', 'Karnataka', 'Near Outer Ring Road', 560103, 1),
(3, 'BLD-303', 'Infinity Park', 'Goregaon East', 'Mumbai', 'Maharashtra', 'Near Western Express', 400063, 2)
ON CONFLICT (id) DO NOTHING;

-- Availability Windows
INSERT INTO availability (id, from_date, to_date) VALUES
(1, '2026-09-14 08:00:00', '2026-09-14 18:00:00'),
(2, '2026-09-14 09:00:00', '2026-09-14 20:00:00'),
(3, '2026-09-15 08:00:00', '2026-09-15 22:00:00')
ON CONFLICT (id) DO NOTHING;

-- Slots
INSERT INTO slot (id, slot_number, floornumber, division_no, building_id, availability_id) VALUES
(1, 'A-101', '1', 'A', 1, 1),
(2, 'A-102', '1', 'A', 1, 2),
(3, 'B-201', '2', 'B', 2, 3)
ON CONFLICT (id) DO NOTHING;

-- Slot Bookings
INSERT INTO slot_booking (id, booking_date, status, slot_id, user_id, availabilty_id) VALUES
(1, '2026-09-14', 'SLOT BOOKED', 1, 2, 1),
(2, '2026-09-14', 'CONFIRMED', 2, 3, 2),
(3, '2026-09-15', 'SLOT BOOKED', 3, 2, 3)
ON CONFLICT (id) DO NOTHING;

-- Ensure sequence values start beyond pre-seeded IDs
SELECT setval('availability_seq', 100, true);
SELECT setval('building_seq', 100, true);
