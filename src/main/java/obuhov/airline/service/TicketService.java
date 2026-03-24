package obuhov.airline.service;

import obuhov.airline.entity.Ticket;
import obuhov.airline.entity.Client;
import obuhov.airline.entity.Flight;
import obuhov.airline.entity.BonusCard;
import obuhov.airline.repository.TicketRepository;
import obuhov.airline.repository.ClientRepository;
import obuhov.airline.repository.FlightRepository;
import obuhov.airline.repository.BonusCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BonusCardRepository bonusCardRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getTicketsByClientId(Integer clientId) {
        return ticketRepository.findByClientId(clientId);
    }

    public List<Ticket> getTicketsByFlightId(Integer flightId) {
        return ticketRepository.findByFlightId(flightId);
    }

    public List<Ticket> getPaidTickets() {
        return ticketRepository.findByIsPaid(1);
    }

    public List<Ticket> getReservedTickets() {
        return ticketRepository.findByIsPaid(0);
    }

    public Ticket createTicket(Ticket ticket) {
        validateTicket(ticket);

        // Check if seat is available
        if (ticketRepository.existsByFlightIdAndSeat(ticket.getFlight().getFlightID(), ticket.getSeat())) {
            throw new IllegalArgumentException("Seat " + ticket.getSeat() + " is already booked");
        }

        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Integer id, Ticket ticketDetails) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        ticket.setSeat(ticketDetails.getSeat());
        ticket.setIsPaid(ticketDetails.getIsPaid());

        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Integer id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }

    public Ticket bookTicket(Integer flightId, Integer clientId, String seat) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        if (!isSeatAvailable(flightId, seat)) {
            throw new IllegalArgumentException("Seat " + seat + " is not available");
        }

        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setClient(client);
        ticket.setSeat(seat);
        ticket.setIsPaid(0); // Reserved

        return ticketRepository.save(ticket);
    }

    public Ticket payTicket(Integer ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setIsPaid(1);
        return ticketRepository.save(ticket);
    }

    public Integer calculateTicketPrice(Integer flightId, Integer clientId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        Integer basePrice = flight.getCost();

        // Apply bonus discount if available
        Optional<BonusCard> bonusCard = bonusCardRepository.findByClientIdAndAirlineId(
                clientId, flight.getAirline().getAirlineID());

        if (bonusCard.isPresent()) {
            Integer discount = bonusCard.get().getDiscount();
            basePrice = basePrice - (basePrice * discount / 100);
        }

        return basePrice;
    }

    private boolean isSeatAvailable(Integer flightId, String seat) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (flight.getAvailableSeats() == null || flight.getAvailableSeats().isEmpty()) {
            return false;
        }

        List<String> availableSeats = Arrays.stream(flight.getAvailableSeats().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        return availableSeats.contains(seat) && !ticketRepository.existsByFlightIdAndSeat(flightId, seat);
    }

    private void validateTicket(Ticket ticket) {
        if (ticket.getFlight() == null) {
            throw new IllegalArgumentException("Flight is required");
        }
        if (ticket.getClient() == null) {
            throw new IllegalArgumentException("Client is required");
        }
        if (ticket.getSeat() == null || ticket.getSeat().isEmpty()) {
            throw new IllegalArgumentException("Seat is required");
        }
        if (ticket.getIsPaid() == null || (ticket.getIsPaid() != 0 && ticket.getIsPaid() != 1)) {
            throw new IllegalArgumentException("Is_paid must be 0 or 1");
        }
    }
}