package obuhov.airline.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityAndUtilityTest {

    @Test
    void clientCustomConstructor_setsFields() {
        Client client = new Client(7, "Иван", "+7-900", "ivan@test.ru", "Москва");
        assertEquals(7, client.getClientID());
        assertEquals("Иван", client.getName());
        assertEquals("+7-900", client.getPhoneNumber());
        assertEquals("ivan@test.ru", client.getEmail());
        assertEquals("Москва", client.getAddress());
    }

    @Test
    void flightCustomConstructor_setsFields() {
        Airport departure = new Airport(1, "Шереметьево", "Москва");
        Airport arrival = new Airport(2, "Пулково", "СПб");
        Airline airline = new Airline(3, "Аэрофлот");

        Flight flight = new Flight(
                5,
                departure,
                arrival,
                airline,
                LocalDate.of(2026, 4, 5),
                LocalDate.of(2026, 4, 5),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                5000,
                "1A,1B"
        );

        assertEquals(5, flight.getFlightID());
        assertEquals(departure, flight.getDepartureAirport());
        assertEquals(arrival, flight.getArrivalAirport());
        assertEquals(airline, flight.getAirline());
        assertEquals(5000, flight.getCost());
        assertEquals("1A,1B", flight.getAvailableSeats());
    }

    @Test
    void ticketHelperMethods_coverPaidAndReservedStates() {
        Ticket ticket = new Ticket();
        ticket.setIsPaid(1);
        assertTrue(ticket.isPaid());
        assertFalse(ticket.isReserved());

        ticket.setIsPaid(0);
        assertFalse(ticket.isPaid());
        assertTrue(ticket.isReserved());

        ticket.setIsPaid(null);
        assertFalse(ticket.isPaid());
        assertFalse(ticket.isReserved());
    }
}
