package obuhov.airline.service;

import obuhov.airline.entity.Flight;
import obuhov.airline.entity.Airport;
import obuhov.airline.entity.Airline;
import obuhov.airline.repository.FlightRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = NONE)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;
    private Airport departureAirport;
    private Airport arrivalAirport;
    private Airline airline;

    @BeforeEach
    public void setUp() {
        departureAirport = new Airport(1, "Шереметьево", "Москва");
        arrivalAirport = new Airport(2, "Пулково", "Санкт-Петербург");
        airline = new Airline(1, "Аэрофлот");

        testFlight = new Flight();
        testFlight.setFlightID(1);
        testFlight.setDepartureAirport(departureAirport);
        testFlight.setArrivalAirport(arrivalAirport);
        testFlight.setAirline(airline);
        testFlight.setDepartureDate(LocalDate.now());
        testFlight.setArrivalDate(LocalDate.now());
        testFlight.setDepartureTime(LocalTime.of(10, 0));
        testFlight.setArrivalTime(LocalTime.of(12, 0));
        testFlight.setCost(5000);
        testFlight.setAvailableSeats("1A,2B,3C");
    }

    @Test
    @DisplayName("Получение всех рейсов")
    public void testGetAllFlights() {
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findAll()).thenReturn(flights);

        List<Flight> result = flightService.getAllFlights();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получение рейса по ID")
    public void testGetFlightById() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));

        Optional<Flight> result = flightService.getFlightById(1);

        assertTrue(result.isPresent());
        assertEquals(5000, result.get().getCost());
    }

    @Test
    @DisplayName("Создание рейса")
    public void testCreateFlight() {
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        Flight result = flightService.createFlight(testFlight);

        assertNotNull(result);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    @DisplayName("Создание рейса без аэропорта вылета - исключение")
    public void testCreateFlightWithoutDepartureAirport() {
        Flight invalidFlight = new Flight();
        invalidFlight.setArrivalAirport(arrivalAirport);
        invalidFlight.setAirline(airline);
        invalidFlight.setCost(5000);

        assertThrows(IllegalArgumentException.class, () -> {
            flightService.createFlight(invalidFlight);
        });
    }

    @Test
    @DisplayName("Получение доступных мест с пустыми элементами (покрытие ветки filter)")
    public void testGetAvailableSeatsFiltersEmptyStrings() {
        Flight flightWithGaps = new Flight();
        flightWithGaps.setFlightID(2);
        // Специально добавляем лишние запятые и пробелы.
        // После split(",") и trim() появятся пустые строки "", что заставит filter сработать в обеих ветках (true/false)
        flightWithGaps.setAvailableSeats("1A, ,2B,, 3C ");

        when(flightRepository.findById(2)).thenReturn(Optional.of(flightWithGaps));

        List<String> seats = flightService.getAvailableSeats(2);

        assertNotNull(seats);
        assertEquals(3, seats.size()); // Останутся только "1A", "2B", "3C"
        assertTrue(seats.contains("1A"));
        assertTrue(seats.contains("2B"));
        assertTrue(seats.contains("3C"));
    }

    @Test
    @DisplayName("Получение доступных мест")
    public void testGetAvailableSeats() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));

        List<String> seats = flightService.getAvailableSeats(1);

        assertNotNull(seats);
        assertEquals(3, seats.size());
        assertTrue(seats.contains("1A"));
    }

    @Test
    @DisplayName("Подсчет количества доступных мест")
    public void testGetAvailableSeatsCount() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));

        int count = flightService.getAvailableSeatsCount(1);

        assertEquals(3, count);
    }

    @Test
    @DisplayName("Поиск рейсов по дате")
    public void testGetFlightsByDate() {
        List<Flight> flights = Arrays.asList(testFlight);
        when(flightRepository.findByDepartureDate(any(LocalDate.class))).thenReturn(flights);

        List<Flight> result = flightService.getFlightsByDate(LocalDate.now());

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Удаление рейса")
    public void testDeleteFlight() {
        when(flightRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> flightService.deleteFlight(1));
        verify(flightRepository, times(1)).deleteById(1);
    }
}