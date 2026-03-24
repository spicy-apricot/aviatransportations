package obuhov.airline.repository;

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
public class FlightRepositoryTest {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    private Flight savedFlight;
    private Airport departureAirport;
    private Airport arrivalAirport;
    private Airline airline;

    @BeforeEach
    public void setUp() {
        departureAirport = new Airport(null, "Шереметьево", "Москва");
        departureAirport = airportRepository.save(departureAirport);

        arrivalAirport = new Airport(null, "Пулково", "Санкт-Петербург");
        arrivalAirport = airportRepository.save(arrivalAirport);

        airline = new Airline(null, "Аэрофлот");
        airline = airlineRepository.save(airline);

        Flight flight = new Flight();
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setAirline(airline);
        flight.setDepartureDate(LocalDate.now());
        flight.setArrivalDate(LocalDate.now());
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setCost(5000);
        flight.setAvailableSeats("1A,2B,3C");
        savedFlight = flightRepository.save(flight);
    }

    @AfterEach
    public void tearDown() {
        flightRepository.deleteAll();
        airportRepository.deleteAll();
        airlineRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание рейса")
    public void testCreateFlight() {
        assertNotNull(savedFlight.getFlightID());
        assertEquals(5000, savedFlight.getCost());
    }

    @Test
    @DisplayName("Поиск рейсов по дате")
    public void testFindByDepartureDate() {
        List<Flight> found = flightRepository.findByDepartureDate(LocalDate.now());
        assertFalse(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск рейсов по маршруту")
    public void testFindByRoute() {
        List<Flight> found = flightRepository.findByRoute("Москва", "Санкт-Петербург");
        assertFalse(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск рейсов по авиакомпании")
    public void testFindByAirlineId() {
        List<Flight> found = flightRepository.findByAirlineId(airline.getAirlineID());
        assertFalse(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск рейсов по диапазону цен")
    public void testFindByCostRange() {
        List<Flight> found = flightRepository.findByCostRange(4000, 6000);
        assertFalse(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск рейсов с доступными местами")
    public void testFindFlightsWithAvailableSeats() {
        List<Flight> found = flightRepository.findFlightsWithAvailableSeats();
        assertFalse(found.isEmpty());
    }
}