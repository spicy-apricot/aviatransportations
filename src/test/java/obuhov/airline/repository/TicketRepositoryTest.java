package obuhov.airline.repository;

import obuhov.airline.entity.Ticket;
import obuhov.airline.entity.Client;
import obuhov.airline.entity.Flight;
import obuhov.airline.entity.Airport;
import obuhov.airline.entity.Airline;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = NONE)
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    private Ticket savedTicket;
    private Client client;
    private Flight flight;

    @BeforeEach
    public void setUp() {
        client = new Client(null, "Тест Клиент", "+7-999-111-22-33", "test@mail.ru", "Москва");
        client = clientRepository.save(client);

        Airport departure = new Airport(null, "Шереметьево", "Москва");
        departure = airportRepository.save(departure);

        Airport arrival = new Airport(null, "Пулково", "СПб");
        arrival = airportRepository.save(arrival);

        Airline airline = new Airline(null, "Аэрофлот");
        airline = airlineRepository.save(airline);

        flight = new Flight();
        flight.setDepartureAirport(departure);
        flight.setArrivalAirport(arrival);
        flight.setAirline(airline);
        flight.setDepartureDate(LocalDate.now());
        flight.setArrivalDate(LocalDate.now());
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setCost(5000);
        flight.setAvailableSeats("1A,2B");
        flight = flightRepository.save(flight);

        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setClient(client);
        ticket.setSeat("1A");
        ticket.setIsPaid(1);
        savedTicket = ticketRepository.save(ticket);
    }

    @AfterEach
    public void tearDown() {
        ticketRepository.deleteAll();
        flightRepository.deleteAll();
        clientRepository.deleteAll();
        airportRepository.deleteAll();
        airlineRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание билета")
    public void testCreateTicket() {
        assertNotNull(savedTicket.getTicketID());
        assertEquals(1, savedTicket.getIsPaid());
        assertTrue(savedTicket.isPaid());
    }

    @Test
    @DisplayName("Поиск билетов по клиенту")
    public void testFindByClientId() {
        List<Ticket> found = ticketRepository.findByClientId(client.getClientID());
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }

    @Test
    @DisplayName("Поиск билетов по рейсу")
    public void testFindByFlightId() {
        List<Ticket> found = ticketRepository.findByFlightId(flight.getFlightID());
        assertFalse(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск оплаченных билетов")
    public void testFindByIsPaid() {
        List<Ticket> paid = ticketRepository.findByIsPaid(1);
        assertFalse(paid.isEmpty());
    }

    @Test
    @DisplayName("Поиск забронированных билетов")
    public void testFindReservedTickets() {
        Ticket reserved = new Ticket();
        reserved.setFlight(flight);
        reserved.setClient(client);
        reserved.setSeat("2B");
        reserved.setIsPaid(0);
        ticketRepository.save(reserved);

        List<Ticket> reservedTickets = ticketRepository.findReservedTicketsByClientId(client.getClientID());
        assertEquals(1, reservedTickets.size());
    }

    @Test
    @DisplayName("Проверка существования билета по рейсу и месту")
    public void testExistsByFlightIdAndSeat() {
        boolean exists = ticketRepository.existsByFlightIdAndSeat(flight.getFlightID(), "1A");
        assertTrue(exists);

        boolean notExists = ticketRepository.existsByFlightIdAndSeat(flight.getFlightID(), "3C");
        assertFalse(notExists);
    }
}