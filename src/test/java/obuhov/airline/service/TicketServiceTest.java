package obuhov.airline.service;

import obuhov.airline.entity.Ticket;
import obuhov.airline.entity.Client;
import obuhov.airline.entity.Flight;
import obuhov.airline.repository.TicketRepository;
import obuhov.airline.repository.ClientRepository;
import obuhov.airline.repository.FlightRepository;
import obuhov.airline.repository.BonusCardRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private BonusCardRepository bonusCardRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket testTicket;
    private Client testClient;
    private Flight testFlight;

    @BeforeEach
    public void setUp() {
        testClient = new Client(1, "Иванов Иван", "+7-900-111-22-33", "ivanov@mail.ru", "Москва");

        testFlight = new Flight();
        testFlight.setFlightID(1);
        testFlight.setCost(5000);
        testFlight.setAvailableSeats("1A,1B,2A,2B,3A,3B");

        testTicket = new Ticket();
        testTicket.setTicketID(1);
        testTicket.setFlight(testFlight);
        testTicket.setClient(testClient);
        testTicket.setSeat("1A");
        testTicket.setIsPaid(0);
    }

    @Test
    @DisplayName("Получение всех билетов")
    public void testGetAllTickets() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketService.getAllTickets();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Получение билетов по клиенту")
    public void testGetTicketsByClientId() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findByClientId(1)).thenReturn(tickets);

        List<Ticket> result = ticketService.getTicketsByClientId(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Получение оплаченных билетов")
    public void testGetPaidTickets() {
        testTicket.setIsPaid(1);
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findByIsPaid(1)).thenReturn(tickets);

        List<Ticket> result = ticketService.getPaidTickets();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Бронирование билета")
    public void testBookTicket() {
        when(flightRepository.findById(1)).thenReturn(Optional.of(testFlight));
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(ticketRepository.existsByFlightIdAndSeat(1, "1A")).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        Ticket result = ticketService.bookTicket(1, 1, "1A");

        assertNotNull(result);
        assertEquals(0, result.getIsPaid());
        assertTrue(result.isReserved());
    }

    @Test
    @DisplayName("Бронирование занятого места - исключение")
    public void testBookTicketSeatNotAvailable() {
        Flight flightWithSeats = new Flight();
        flightWithSeats.setFlightID(1);
        flightWithSeats.setCost(5000);
        flightWithSeats.setAvailableSeats("1A,1B,2A,2B");

        when(flightRepository.findById(1)).thenReturn(Optional.of(flightWithSeats));
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(ticketRepository.existsByFlightIdAndSeat(1, "1A")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.bookTicket(1, 1, "1A");
        });
    }

    @Test
    @DisplayName("Оплата билета")
    public void testPayTicket() {
        testTicket.setIsPaid(0);
        Ticket paidTicket = new Ticket();
        paidTicket.setTicketID(1);
        paidTicket.setFlight(testFlight);
        paidTicket.setClient(testClient);
        paidTicket.setSeat("1A");
        paidTicket.setIsPaid(1);

        when(ticketRepository.findById(1)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(paidTicket);

        Ticket result = ticketService.payTicket(1);

        assertNotNull(result);
        assertEquals(1, result.getIsPaid());
        assertTrue(result.isPaid());
    }

    @Test
    @DisplayName("Создание билета без рейса - исключение")
    public void testCreateTicketWithoutFlight() {
        Ticket invalidTicket = new Ticket();
        invalidTicket.setClient(testClient);
        invalidTicket.setSeat("1A");
        invalidTicket.setIsPaid(0);

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(invalidTicket);
        });
    }

    @Test
    @DisplayName("Удаление билета")
    public void testDeleteTicket() {
        when(ticketRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> ticketService.deleteTicket(1));
        verify(ticketRepository, times(1)).deleteById(1);
    }
}