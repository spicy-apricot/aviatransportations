package obuhov.airline.service;

import obuhov.airline.entity.*;
import obuhov.airline.repository.FlightRepository;
import obuhov.airline.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceAdditionalTest {

    @Mock private FlightRepository flightRepository;
    @Mock private TicketRepository ticketRepository;
    @InjectMocks private FlightService flightService;

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = new Flight();
        flight.setFlightID(1);
        flight.setDepartureAirport(new Airport(1, "Шереметьево", "Москва"));
        flight.setArrivalAirport(new Airport(2, "Пулково", "СПб"));
        flight.setAirline(new Airline(1, "Аэрофлот"));
        flight.setDepartureDate(LocalDate.now());
        flight.setArrivalDate(LocalDate.now());
        flight.setDepartureTime(LocalTime.of(10, 0));
        flight.setArrivalTime(LocalTime.of(12, 0));
        flight.setCost(5000);
        flight.setAvailableSeats("1A, 2B , ,3C");
    }

    @Test
    void repositoryDelegatingMethods_areCovered() {
        when(flightRepository.findByRoute("Москва", "СПб")).thenReturn(List.of(flight));
        when(flightRepository.findByAirlineId(1)).thenReturn(List.of(flight));
        when(flightRepository.findByCostRange(1000, 6000)).thenReturn(List.of(flight));
        when(flightRepository.findFlightsWithAvailableSeats()).thenReturn(List.of(flight));
        when(ticketRepository.existsByFlightIdAndSeat(1, "1A")).thenReturn(false);
        when(ticketRepository.existsByFlightIdAndSeat(1, "9Z")).thenReturn(true);

        assertEquals(1, flightService.getFlightsByRoute("Москва", "СПб").size());
        assertEquals(1, flightService.getFlightsByAirlineId(1).size());
        assertEquals(1, flightService.getFlightsByCostRange(1000, 6000).size());
        assertEquals(1, flightService.getFlightsWithAvailableSeats().size());
        assertTrue(flightService.isSeatAvailable(1, "1A"));
        assertFalse(flightService.isSeatAvailable(1, "9Z"));
    }

    @Test
    void updateFlight_updatesAllFields() {
        Flight updated = new Flight();
        updated.setDepartureAirport(new Airport(10, "Внуково", "Москва"));
        updated.setArrivalAirport(new Airport(11, "Сочи", "Сочи"));
        updated.setAirline(new Airline(2, "S7"));
        updated.setDepartureDate(LocalDate.of(2026, 5, 1));
        updated.setArrivalDate(LocalDate.of(2026, 5, 1));
        updated.setDepartureTime(LocalTime.of(8, 0));
        updated.setArrivalTime(LocalTime.of(11, 0));
        updated.setCost(9000);
        updated.setAvailableSeats("4A,4B");

        when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
        when(flightRepository.save(any(Flight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Flight result = flightService.updateFlight(1, updated);
        assertEquals(9000, result.getCost());
        assertEquals("4A,4B", result.getAvailableSeats());
        assertEquals("Внуково", result.getDepartureAirport().getName());
    }

    @Test
    void updateFlight_throwsWhenNotFound() {
        when(flightRepository.findById(1)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> flightService.updateFlight(1, flight));
        assertTrue(ex.getMessage().contains("Flight not found"));
    }

    @Test
    void deleteFlight_throwsWhenNotFound() {
        when(flightRepository.existsById(7)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> flightService.deleteFlight(7));
    }

    @Test
    void getAvailableSeats_handlesEmptyStringAndMissingFlight() {
        flight.setAvailableSeats("");
        when(flightRepository.findById(1)).thenReturn(Optional.of(flight));
        assertTrue(flightService.getAvailableSeats(1).isEmpty());

        when(flightRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> flightService.getAvailableSeats(2));
    }

    @Test
    void createFlight_validatesMoreBranches() {
        Flight invalidNoArrival = new Flight();
        invalidNoArrival.setDepartureAirport(new Airport());
        invalidNoArrival.setAirline(new Airline());
        invalidNoArrival.setCost(1000);
        assertThrows(IllegalArgumentException.class, () -> flightService.createFlight(invalidNoArrival));

        Flight invalidNoAirline = new Flight();
        invalidNoAirline.setDepartureAirport(new Airport());
        invalidNoAirline.setArrivalAirport(new Airport());
        invalidNoAirline.setCost(1000);
        assertThrows(IllegalArgumentException.class, () -> flightService.createFlight(invalidNoAirline));

        Flight invalidCost = new Flight();
        invalidCost.setDepartureAirport(new Airport());
        invalidCost.setArrivalAirport(new Airport());
        invalidCost.setAirline(new Airline());
        invalidCost.setCost(0);
        assertThrows(IllegalArgumentException.class, () -> flightService.createFlight(invalidCost));
    }
}
