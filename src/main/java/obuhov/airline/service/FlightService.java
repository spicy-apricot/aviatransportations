package obuhov.airline.service;

import obuhov.airline.entity.Flight;
import obuhov.airline.entity.Ticket;
import obuhov.airline.repository.FlightRepository;
import obuhov.airline.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightById(Integer id) {
        return flightRepository.findById(id);
    }

    public List<Flight> getFlightsByDate(LocalDate date) {
        return flightRepository.findByDepartureDate(date);
    }

    public List<Flight> getFlightsByRoute(String fromCity, String toCity) {
        return flightRepository.findByRoute(fromCity, toCity);
    }

    public List<Flight> getFlightsByAirlineId(Integer airlineId) {
        return flightRepository.findByAirlineId(airlineId);
    }

    public List<Flight> getFlightsByCostRange(Integer minCost, Integer maxCost) {
        return flightRepository.findByCostRange(minCost, maxCost);
    }

    public List<Flight> getFlightsWithAvailableSeats() {
        return flightRepository.findFlightsWithAvailableSeats();
    }

    public Flight createFlight(Flight flight) {
        validateFlight(flight);
        return flightRepository.save(flight);
    }

    public Flight updateFlight(Integer id, Flight flightDetails) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));

        validateFlight(flightDetails);
        flight.setArrivalAirport(flightDetails.getArrivalAirport());
        flight.setDepartureAirport(flightDetails.getDepartureAirport());
        flight.setAirline(flightDetails.getAirline());
        flight.setArrivalDate(flightDetails.getArrivalDate());
        flight.setDepartureDate(flightDetails.getDepartureDate());
        flight.setArrivalTime(flightDetails.getArrivalTime());
        flight.setDepartureTime(flightDetails.getDepartureTime());
        flight.setCost(flightDetails.getCost());
        flight.setAvailableSeats(flightDetails.getAvailableSeats());

        return flightRepository.save(flight);
    }

    public void deleteFlight(Integer id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Flight not found with id: " + id);
        }
        flightRepository.deleteById(id);
    }

    public List<String> getAvailableSeats(Integer flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (flight.getAvailableSeats() == null || flight.getAvailableSeats().isEmpty()) {
            return List.of();
        }

        return Arrays.stream(flight.getAvailableSeats().split(","))
                .map(String::trim)
                .filter(seat -> !seat.isEmpty())
                .collect(Collectors.toList());
    }

    public boolean isSeatAvailable(Integer flightId, String seat) {
        return !ticketRepository.existsByFlightIdAndSeat(flightId, seat);
    }

    public int getAvailableSeatsCount(Integer flightId) {
        return getAvailableSeats(flightId).size();
    }

    private void validateFlight(Flight flight) {
        if (flight.getDepartureAirport() == null) {
            throw new IllegalArgumentException("Departure airport is required");
        }
        if (flight.getArrivalAirport() == null) {
            throw new IllegalArgumentException("Arrival airport is required");
        }
        if (flight.getAirline() == null) {
            throw new IllegalArgumentException("Airline is required");
        }
        if (flight.getCost() == null || flight.getCost() <= 0) {
            throw new IllegalArgumentException("Cost must be positive");
        }
    }
}