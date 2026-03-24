INSERT INTO Airport (Name, City) VALUES
     ('Шереметьево', 'Москва'),
     ('Пулково', 'Санкт-Петербург'),
     ('Кольцово', 'Екатеринбург');

INSERT INTO Airline (Name) VALUES
       ('Аэрофлот'),
       ('S7 Airlines'),
       ('Уральские авиалинии');

INSERT INTO Client (Name, Phone_number, Email, Address) VALUES
        ('Иванов Иван', '+7-900-111-22-33', 'ivanov@mail.ru', 'Москва, ул. Ленина 1'),
        ('Петрова Анна', '+7-900-444-55-66', 'petrova@mail.ru', 'СПб, Невский 25'),
        ('Сидоров Петр', '+7-900-777-88-99', 'sidorov@mail.ru', 'Екатеринбург, Мира 10'),
        ('Козлова Мария', '+7-900-333-22-11', 'kozlova@mail.ru', 'Москва, Арбат 5');

INSERT INTO Flight (Arrival_airport, Departure_airport, AirlineID, Arrival_date, Departure_date, Arrival_time, Departure_time, Cost, Available_seats) VALUES
      (2, 1, 1, '2026-03-15', '2026-03-15', '14:30', '12:00', 8500, '1A,1B,12C,15D'),
      (3, 1, 3, '2026-03-16', '2026-03-16', '18:00', '15:30', 7200, '2A,5B,8C'),
      (1, 2, 2, '2026-03-17', '2026-03-17', '10:00', '08:30', 6800, '3A,7D,11F,20A'),
      (3, 2, 1, '2026-03-18', '2026-03-18', '20:00', '17:30', 9500, '4B,9C,14E'),
      (1, 3, 3, '2026-03-20', '2026-03-20', '12:30', '10:00', 7500, '1C,6D,10A,18B');

INSERT INTO Ticket (FlightID, ClientID, Seat, Is_paid) VALUES
       (1, 1, '1A', 1),  -- куплен
       (1, 2, '1B', 0),  -- забронирован
       (2, 3, '2A', 1),  -- куплен
       (3, 1, '3A', 0),  -- забронирован
       (4, 4, '4B', 1);  -- куплен

INSERT INTO Bonus_Card (AirlineID, ClientID, Discount) VALUES
       (1, 1, 10),
       (1, 2, 5),
       (2, 3, 15),
       (3, 4, 8);

INSERT INTO Traveled (ClientID, AirlineID, Distance) VALUES
     (1, 1, 2500),
     (1, 2, 1200),
     (2, 1, 3800),
     (3, 3, 4500),
     (4, 3, 2100);