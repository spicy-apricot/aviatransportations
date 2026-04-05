package obuhov.airline.service;

import obuhov.airline.entity.*;
import obuhov.airline.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceAdditionalTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private FlightRepository flightRepository;
    @Mock private BonusCardRepository bonusCardRepository;
    @InjectMocks private TicketService ticketService;

    private Client client;
    private Flight flight;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        client = new Client(1, "Иван", "+7-900", "ivan@test.ru", "Москва");
        Airline airline = new Airline(3, "Аэрофлот");
        flight = new Flight();
        flight.setFlightID(2);
        flight.setAirline(airline);
        flight.setCost(10000);
        flight.setAvailableSeats("1A,1B");

        ticket = new Ticket();
        ticket.setTicketID(5);
        ticket.setClient(client);
        ticket.setFlight(flight);
        ticket.setSeat("1A");
        ticket.setIsPaid(0);
    }

    @Test
    void simpleDelegatingMethods_areCovered() {
        when(ticketRepository.findById(5)).thenReturn(Optional.of(ticket));
        when(ticketRepository.findByFlightId(2)).thenReturn(List.of(ticket));
        when(ticketRepository.findByIsPaid(0)).thenReturn(List.of(ticket));

        assertTrue(ticketService.getTicketById(5).isPresent());
        assertEquals(1, ticketService.getTicketsByFlightId(2).size());
        assertEquals(1, ticketService.getReservedTickets().size());
    }

    @Test
    void createTicket_successAndDuplicateSeatBranch() {
        when(ticketRepository.existsByFlightIdAndSeat(2, "1A")).thenReturn(false).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket created = ticketService.createTicket(ticket);
        assertEquals("1A", created.getSeat());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(ticket));
        assertTrue(ex.getMessage().contains("already booked"));
    }

    @Test
    void createTicket_validationBranches() {
        Ticket noClient = new Ticket();
        noClient.setFlight(flight);
        noClient.setSeat("1A");
        noClient.setIsPaid(0);
        assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(noClient));

        Ticket noSeat = new Ticket();
        noSeat.setFlight(flight);
        noSeat.setClient(client);
        noSeat.setSeat("");
        noSeat.setIsPaid(0);
        assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(noSeat));

        Ticket badPaid = new Ticket();
        badPaid.setFlight(flight);
        badPaid.setClient(client);
        badPaid.setSeat("1A");
        badPaid.setIsPaid(2);
        assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(badPaid));
    }

    @Test
    void updateTicket_successAndNotFound() {
        Ticket details = new Ticket();
        details.setSeat("1B");
        details.setIsPaid(1);

        when(ticketRepository.findById(5)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Ticket updated = ticketService.updateTicket(5, details);
        assertEquals("1B", updated.getSeat());
        assertEquals(1, updated.getIsPaid());

        when(ticketRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ticketService.updateTicket(99, details));
    }

    @Test
    void deleteTicket_notFound() {
        when(ticketRepository.existsById(9)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> ticketService.deleteTicket(9));
    }

    @Test
    void bookTicket_notFoundAndUnavailableBranches() {
        when(flightRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ticketService.bookTicket(2, 1, "1A"));

        when(flightRepository.findById(2)).thenReturn(Optional.of(flight));
        when(clientRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ticketService.bookTicket(2, 1, "1A"));

        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(ticketRepository.existsByFlightIdAndSeat(2, "1A")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> ticketService.bookTicket(2, 1, "1A"));

        flight.setAvailableSeats(null);
        when(flightRepository.findById(2)).thenReturn(Optional.of(flight));
        assertThrows(IllegalArgumentException.class, () -> ticketService.bookTicket(2, 1, "1A"));
    }

    @Test
    void payTicket_notFound() {
        when(ticketRepository.findById(100)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ticketService.payTicket(100));
    }

    @Test
    void calculateTicketPrice_withAndWithoutDiscount() {
        BonusCard bonusCard = new BonusCard();
        bonusCard.setDiscount(20);

        when(flightRepository.findById(2)).thenReturn(Optional.of(flight));
        when(bonusCardRepository.findByClientIdAndAirlineId(1, 3)).thenReturn(Optional.of(bonusCard));
        assertEquals(8000, ticketService.calculateTicketPrice(2, 1));

        when(bonusCardRepository.findByClientIdAndAirlineId(1, 3)).thenReturn(Optional.empty());
        assertEquals(10000, ticketService.calculateTicketPrice(2, 1));

        when(flightRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> ticketService.calculateTicketPrice(99, 1));
    }
}
