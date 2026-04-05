-- ============================================
-- Удаление таблиц (для тестирования)
-- ============================================
DROP TABLE IF EXISTS Traveled CASCADE;
DROP TABLE IF EXISTS Bonus_Card CASCADE;
DROP TABLE IF EXISTS Ticket CASCADE;
DROP TABLE IF EXISTS Flight CASCADE;
DROP TABLE IF EXISTS Client CASCADE;
DROP TABLE IF EXISTS Airline CASCADE;
DROP TABLE IF EXISTS Airport CASCADE;


-- ============================================
-- СОЗДАНИЕ ТАБЛИЦ
-- ============================================

CREATE TABLE Airport (
     AirportID SERIAL PRIMARY KEY,
     Name VARCHAR(300),
     City VARCHAR(300)
);

CREATE TABLE Airline (
     AirlineID SERIAL PRIMARY KEY,
     Name VARCHAR(100)
);

CREATE TABLE Client (
    ClientID SERIAL PRIMARY KEY,
    Name VARCHAR(100),
    Phone_number VARCHAR(100),
    Email VARCHAR(100),
    Address VARCHAR(100)
);

CREATE TABLE Flight (
    FlightID SERIAL PRIMARY KEY,
    Arrival_airport INT REFERENCES Airport(AirportID),
    Departure_airport INT REFERENCES Airport(AirportID),
    AirlineID INT REFERENCES Airline(AirlineID),
    Arrival_date DATE,
    Departure_date DATE,
    Arrival_time TIME,
    Departure_time TIME,
    Cost INT,
    Available_seats TEXT
);

CREATE TABLE Ticket (
    TicketID SERIAL PRIMARY KEY,
    FlightID INT REFERENCES Flight(FlightID),
    ClientID INT REFERENCES Client(ClientID),
    Seat VARCHAR(10),
    Is_paid INT CHECK (Is_paid IN (0, 1))
);

CREATE TABLE Bonus_Card (
    CardID SERIAL PRIMARY KEY,
    AirlineID INT REFERENCES Airline(AirlineID),
    ClientID INT REFERENCES Client(ClientID),
    Discount INT
);

CREATE TABLE Traveled (
  RecordID SERIAL PRIMARY KEY,
  ClientID INT REFERENCES Client(ClientID),
  AirlineID INT REFERENCES Airline(AirlineID),
  Distance INT CHECK (Distance > 0)
);