package obuhov.airline.system;

import obuhov.airline.entity.Airline;
import obuhov.airline.entity.Airport;
import obuhov.airline.entity.BonusCard;
import obuhov.airline.entity.Client;
import obuhov.airline.entity.Flight;
import obuhov.airline.entity.Ticket;
import obuhov.airline.entity.Traveled;
import obuhov.airline.repository.AirlineRepository;
import obuhov.airline.repository.AirportRepository;
import obuhov.airline.repository.BonusCardRepository;
import obuhov.airline.repository.ClientRepository;
import obuhov.airline.repository.FlightRepository;
import obuhov.airline.repository.TicketRepository;
import obuhov.airline.repository.TraveledRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:systemtest;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class FlightSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BonusCardRepository bonusCardRepository;

    @Autowired
    private TraveledRepository traveledRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

    private Client ivan;
    private Client anna;
    private Client petr;
    private Client maria;
    private Flight flightOne;
    private Flight flightTwo;
    private Flight flightFour;
    private Flight flightFive;

    @BeforeEach
    void setUpData() {
        ticketRepository.deleteAll();
        traveledRepository.deleteAll();
        bonusCardRepository.deleteAll();
        flightRepository.deleteAll();
        clientRepository.deleteAll();
        airlineRepository.deleteAll();
        airportRepository.deleteAll();

        Airport sheremetyevo = airportRepository.save(airport("Шереметьево", "Москва"));
        Airport pulkovo = airportRepository.save(airport("Пулково", "Санкт-Петербург"));
        Airport koltsovo = airportRepository.save(airport("Кольцово", "Екатеринбург"));

        Airline aeroflot = airlineRepository.save(airline("Аэрофлот"));
        Airline s7 = airlineRepository.save(airline("S7 Airlines"));
        Airline ural = airlineRepository.save(airline("Уральские авиалинии"));

        ivan = clientRepository.save(client("Иванов Иван", "+7-900-111-22-33", "ivanov@mail.ru", "Москва, ул. Ленина 1"));
        anna = clientRepository.save(client("Петрова Анна", "+7-900-444-55-66", "petrova@mail.ru", "СПб, Невский 25"));
        petr = clientRepository.save(client("Сидоров Петр", "+7-900-777-88-99", "sidorov@mail.ru", "Екатеринбург, Мира 10"));
        maria = clientRepository.save(client("Козлова Мария", "+7-900-333-22-11", "kozlova@mail.ru", "Москва, Арбат 5"));

        flightOne = flightRepository.save(flight(sheremetyevo, pulkovo, aeroflot,
                LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 15),
                LocalTime.of(12, 0), LocalTime.of(14, 30), 8500, "12C,15D"));
        flightTwo = flightRepository.save(flight(sheremetyevo, koltsovo, ural,
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 16),
                LocalTime.of(15, 30), LocalTime.of(18, 0), 7200, "5B,8C"));
        Flight flightThree = flightRepository.save(flight(pulkovo, sheremetyevo, s7,
                LocalDate.of(2026, 3, 17), LocalDate.of(2026, 3, 17),
                LocalTime.of(8, 30), LocalTime.of(10, 0), 6800, "7D,11F,20A"));
        flightFour = flightRepository.save(flight(pulkovo, koltsovo, aeroflot,
                LocalDate.of(2026, 3, 18), LocalDate.of(2026, 3, 18),
                LocalTime.of(17, 30), LocalTime.of(20, 0), 9500, "9C,14E"));
        flightFive = flightRepository.save(flight(koltsovo, sheremetyevo, ural,
                LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0), LocalTime.of(12, 30), 7500, "1C,6D,10A,18B"));

        ticketRepository.save(ticket(flightOne, ivan, "1A", 1));
        ticketRepository.save(ticket(flightOne, anna, "1B", 0));
        ticketRepository.save(ticket(flightTwo, petr, "2A", 1));
        ticketRepository.save(ticket(flightThree, ivan, "3A", 0));
        ticketRepository.save(ticket(flightFour, maria, "4B", 1));

        bonusCardRepository.save(bonusCard(aeroflot, ivan, 10));
        bonusCardRepository.save(bonusCard(aeroflot, anna, 5));
        bonusCardRepository.save(bonusCard(s7, petr, 15));
        bonusCardRepository.save(bonusCard(ural, maria, 8));

        traveledRepository.save(traveled(ivan, aeroflot, 2500));
        traveledRepository.save(traveled(ivan, s7, 1200));
        traveledRepository.save(traveled(anna, aeroflot, 3800));
        traveledRepository.save(traveled(petr, ural, 4500));
        traveledRepository.save(traveled(maria, ural, 2100));
    }

    @Test
    void homePageShowsFlightDataAndSearchFiltersResults() {
        String homeBody = body(get("/"));

        assertContains(homeBody, "Добро пожаловать в AviaTransport!");
        assertContains(homeBody, "Москва (Шереметьево)");
        assertContains(homeBody, "Санкт-Петербург (Пулково)");
        assertContains(homeBody, "8500 ₽");

        String filteredBody = body(get("/?from=Москва&to=Санкт-Петербург&date=2026-03-15"));

        assertContains(filteredBody, "Найдено рейсов: 1");
        assertContains(filteredBody, "Москва (Шереметьево)");
        assertContains(filteredBody, "Санкт-Петербург (Пулково)");
        assertNotContains(filteredBody, "Екатеринбург (Кольцово)");
        assertContains(filteredBody, "value=\"2026-03-15\"");
    }

    @Test
    void clientPagesShowSeedDataAndSearchResults() {
        String listBody = body(get("/clients"));

        assertContains(listBody, "Иванов Иван");
        assertContains(listBody, "Петрова Анна");
        assertContains(listBody, "+7-900-111-22-33");

        String searchBody = body(get("/clients?search=777-88-99"));

        assertContains(searchBody, "Сидоров Петр");
        assertNotContains(searchBody, "Иванов Иван");

        String viewBody = body(get("/clients/" + ivan.getClientID()));

        assertContains(viewBody, "Иванов Иван");
        assertContains(viewBody, "ivanov@mail.ru");
        assertContains(viewBody, "Аэрофлот");
        assertContains(viewBody, "2500 км");
        assertContains(viewBody, "Билеты клиента");
        assertContains(viewBody, "#" + flightOne.getFlightID());
        assertContains(viewBody, "1A");
        assertContains(viewBody, "Оплачен");
    }

    @Test
    void clientCrudFlowChangesVisibleDataOnPages() {
        postForm("/clients", form(
                "name", "Тестовый Клиент",
                "phoneNumber", "+7-900-555-44-33",
                "email", "test-client@mail.ru",
                "address", "Москва, ул. Тестовая 7"
        ));

        Client created = clientRepository.findByNameAndPhoneNumber("Тестовый Клиент", "+7-900-555-44-33")
                .orElseThrow();

        String listAfterCreate = body(get("/clients"));
        assertContains(listAfterCreate, "Тестовый Клиент");
        assertContains(listAfterCreate, "+7-900-555-44-33");

        postForm("/clients", form(
                "clientID", created.getClientID().toString(),
                "name", "Обновленный Клиент",
                "phoneNumber", "+7-900-000-11-22",
                "email", "updated-client@mail.ru",
                "address", "Казань, ул. Новая 10"
        ));

        String viewAfterUpdate = body(get("/clients/" + created.getClientID()));
        assertContains(viewAfterUpdate, "Обновленный Клиент");
        assertContains(viewAfterUpdate, "+7-900-000-11-22");
        assertContains(viewAfterUpdate, "updated-client@mail.ru");
        assertContains(viewAfterUpdate, "Казань, ул. Новая 10");

        postForm("/clients/" + created.getClientID() + "/delete", new LinkedMultiValueMap<>());

        String listAfterDelete = body(get("/clients"));
        assertNotContains(listAfterDelete, "Обновленный Клиент");
        assertFalse(clientRepository.findById(created.getClientID()).isPresent());
    }

    @Test
    void flightPagesShowSeedDataAndFilteringWorks() {
        String listBody = body(get("/flights"));

        assertContains(listBody, "#" + flightOne.getFlightID());
        assertContains(listBody, "Москва");
        assertContains(listBody, "Санкт-Петербург");
        assertContains(listBody, "Аэрофлот");
        assertContains(listBody, "8500 ₽");

        String filteredBody = body(get("/flights?airlineId=" + flightFive.getAirline().getAirlineID() + "&minCost=7000&maxCost=8000"));

        assertContains(filteredBody, "#" + flightTwo.getFlightID());
        assertContains(filteredBody, "#" + flightFive.getFlightID());
        assertContains(filteredBody, "Уральские авиалинии");
        assertNotContains(filteredBody, "#" + flightOne.getFlightID());
        assertNotContains(filteredBody, "#" + flightFour.getFlightID());

        String viewBody = body(get("/flights/" + flightOne.getFlightID()));

        assertContains(viewBody, "Рейс #" + flightOne.getFlightID());
        assertContains(viewBody, "Стоимость: <strong>8500 руб.</strong>");
        assertContains(viewBody, "Купить 12C");
        assertContains(viewBody, "Купить 15D");
    }

    @Test
    void flightCrudFlowChangesVisibleDataOnPages() {
        postForm("/flights", form(
                "departureAirportId", airportRepository.findByCity("Москва").get(0).getAirportID().toString(),
                "arrivalAirportId", airportRepository.findByCity("Екатеринбург").get(0).getAirportID().toString(),
                "airlineId", airlineRepository.findByNameContaining("S7").get(0).getAirlineID().toString(),
                "departureDate", "2026-04-10",
                "arrivalDate", "2026-04-10",
                "departureTime", "09:15",
                "arrivalTime", "12:05",
                "cost", "12345",
                "availableSeats", "21A,21B"
        ));

        Flight created = flightRepository.findAll().stream()
                .filter(flight -> Integer.valueOf(12345).equals(flight.getCost()) && "21A,21B".equals(flight.getAvailableSeats()))
                .max(Comparator.comparing(Flight::getFlightID))
                .orElseThrow();

        String listAfterCreate = body(get("/flights"));
        assertContains(listAfterCreate, "#" + created.getFlightID());
        assertContains(listAfterCreate, "12345 ₽");
        assertContains(listAfterCreate, "S7 Airlines");

        postForm("/flights", form(
                "flightID", created.getFlightID().toString(),
                "departureAirportId", airportRepository.findByCity("Санкт-Петербург").get(0).getAirportID().toString(),
                "arrivalAirportId", airportRepository.findByCity("Москва").get(0).getAirportID().toString(),
                "airlineId", airlineRepository.findByNameContaining("Аэрофлот").get(0).getAirlineID().toString(),
                "departureDate", "2026-04-11",
                "arrivalDate", "2026-04-11",
                "departureTime", "13:40",
                "arrivalTime", "15:10",
                "cost", "13579",
                "availableSeats", "22C,22D,22E"
        ));

        String viewAfterUpdate = body(get("/flights/" + created.getFlightID()));
        assertContains(viewAfterUpdate, "Санкт-Петербург - Москва");
        assertContains(viewAfterUpdate, "Авиакомпания: Аэрофлот");
        assertContains(viewAfterUpdate, "Стоимость: <strong>13579 руб.</strong>");
        assertContains(viewAfterUpdate, "Купить 22C");
        assertContains(viewAfterUpdate, "Купить 22E");

        postForm("/flights/" + created.getFlightID() + "/delete", new LinkedMultiValueMap<>());

        String listAfterDelete = body(get("/flights"));
        assertNotContains(listAfterDelete, "#" + created.getFlightID());
        assertFalse(flightRepository.findById(created.getFlightID()).isPresent());
    }

    @Test
    void orderFlowCreatesClientAndTicketAndRemovesPurchasedSeat() {
        String flightViewBefore = body(get("/flights/" + flightOne.getFlightID()));
        assertContains(flightViewBefore, "Купить 12C");

        String checkout = body(get("/flights/" + flightOne.getFlightID() + "/buy?seat=12C"));
        assertContains(checkout, "Оформление билета");
        assertContains(checkout, "Рейс №" + flightOne.getFlightID());
        assertContains(checkout, "Место:</strong> 12C");
        assertContains(checkout, "Стоимость:</strong> 8500 ₽");

        String payment = body(postForm("/order/confirm", form(
                "flightId", flightOne.getFlightID().toString(),
                "seat", "12C",
                "clientName", "Новый Покупатель",
                "clientPhone", "+7-901-123-45-67",
                "flightCost", "8500"
        )));
        assertContains(payment, "Оплата заказа");
        assertContains(payment, "Новый Покупатель");
        assertContains(payment, "12C");
        assertContains(payment, "8500 ₽");

        String success = body(postForm("/order/pay", form(
                "flightId", flightOne.getFlightID().toString(),
                "seat", "12C",
                "clientName", "Новый Покупатель",
                "clientPhone", "+7-901-123-45-67",
                "flightCost", "8500",
                "cardNumber", "4111111111111111",
                "cardExpiry", "12/30",
                "cardCvv", "123"
        )));
        assertContains(success, "Заказ успешно оформлен");
        assertContains(success, "Новый Покупатель");
        assertContains(success, "12C");
        assertContains(success, "8500 ₽");

        Client createdClient = clientRepository.findByNameAndPhoneNumber("Новый Покупатель", "+7-901-123-45-67")
                .orElseThrow();
        Ticket createdTicket = ticketRepository.findByClientId(createdClient.getClientID()).stream()
                .filter(ticket -> ticket.getFlight().getFlightID().equals(flightOne.getFlightID()) && "12C".equals(ticket.getSeat()))
                .findFirst()
                .orElseThrow();

        assertTrue(createdTicket.isPaid());

        String clientsPage = body(get("/clients"));
        assertContains(clientsPage, "Новый Покупатель");

        String clientView = body(get("/clients/" + createdClient.getClientID()));
        assertContains(clientView, "Билеты клиента");
        assertContains(clientView, "#" + flightOne.getFlightID());
        assertContains(clientView, "12C");
        assertContains(clientView, "Оплачен");

        String flightViewAfter = body(get("/flights/" + flightOne.getFlightID()));
        assertNotContains(flightViewAfter, "Купить 12C");
        assertContains(flightViewAfter, "Купить 15D");
    }

    @Test
    void unavailableSeatShowsBusinessErrorOnFlightPage() {
        String response = body(get("/flights/" + flightOne.getFlightID() + "/buy?seat=1A"));

        assertContains(response, "Место 1A уже занято");
        assertContains(response, "Рейс #" + flightOne.getFlightID());
        assertContains(response, "Купить 12C");
    }

    @Test
    void invalidFlightPaymentShowsFailurePageWithErrorDetails() {
        String failure = body(postForm("/order/pay", form(
                "flightId", "999",
                "seat", "99Z",
                "clientName", "Ошибка Тест",
                "clientPhone", "+7-900-000-00-00",
                "flightCost", "7777",
                "cardNumber", "4111111111111111",
                "cardExpiry", "12/30",
                "cardCvv", "123"
        )));

        assertContains(failure, "Ошибка оплаты");
        assertContains(failure, "Flight not found");
        assertContains(failure, "#999");
        assertContains(failure, "7777 ₽");
    }

    private Airport airport(String name, String city) {
        Airport airport = new Airport();
        airport.setName(name);
        airport.setCity(city);
        return airport;
    }

    private Airline airline(String name) {
        Airline airline = new Airline();
        airline.setName(name);
        return airline;
    }

    private Client client(String name, String phone, String email, String address) {
        Client client = new Client();
        client.setName(name);
        client.setPhoneNumber(phone);
        client.setEmail(email);
        client.setAddress(address);
        return client;
    }

    private Flight flight(Airport departure, Airport arrival, Airline airline,
                          LocalDate departureDate, LocalDate arrivalDate,
                          LocalTime departureTime, LocalTime arrivalTime,
                          Integer cost, String availableSeats) {
        Flight flight = new Flight();
        flight.setDepartureAirport(departure);
        flight.setArrivalAirport(arrival);
        flight.setAirline(airline);
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setCost(cost);
        flight.setAvailableSeats(availableSeats);
        return flight;
    }

    private Ticket ticket(Flight flight, Client client, String seat, Integer isPaid) {
        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setClient(client);
        ticket.setSeat(seat);
        ticket.setIsPaid(isPaid);
        return ticket;
    }

    private BonusCard bonusCard(Airline airline, Client client, Integer discount) {
        BonusCard bonusCard = new BonusCard();
        bonusCard.setAirline(airline);
        bonusCard.setClient(client);
        bonusCard.setDiscount(discount);
        return bonusCard;
    }

    private Traveled traveled(Client client, Airline airline, Integer distance) {
        Traveled traveled = new Traveled();
        traveled.setClient(client);
        traveled.setAirline(airline);
        traveled.setDistance(distance);
        return traveled;
    }

    private ResponseEntity<String> get(String path) {
        return rest.getForEntity(url(path), String.class);
    }

    private ResponseEntity<String> postForm(String path, MultiValueMap<String, String> form) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        return rest.exchange(url(path), HttpMethod.POST, entity, String.class);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private String body(ResponseEntity<String> response) {
        assertNotNull(response.getBody());
        return response.getBody();
    }

    private MultiValueMap<String, String> form(String... pairs) {
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            form.add(pairs[i], pairs[i + 1]);
        }
        return form;
    }

    private void assertContains(String actual, String expected) {
        assertTrue(actual.contains(expected), () -> "Expected to find '" + expected + "' in response body");
    }

    private void assertNotContains(String actual, String expected) {
        assertFalse(actual.contains(expected), () -> "Did not expect to find '" + expected + "' in response body");
    }
}
