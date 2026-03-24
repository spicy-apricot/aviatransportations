package obuhov.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "Flight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FlightID")
    private Integer flightID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Arrival_airport")
    private Airport arrivalAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Departure_airport")
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AirlineID")
    private Airline airline;

    @Column(name = "Arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "Departure_date")
    private LocalDate departureDate;

    @Column(name = "Arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "Departure_time")
    private LocalTime departureTime;

    @Column(name = "Cost")
    private Integer cost;

    @Column(name = "Available_seats", columnDefinition = "TEXT")
    private String availableSeats;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    public Flight(Integer flightID, Airport departureAirport, Airport arrivalAirport,
                  Airline airline, LocalDate departureDate, LocalDate arrivalDate,
                  LocalTime departureTime, LocalTime arrivalTime, Integer cost, String availableSeats) {
        this.flightID = flightID;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.airline = airline;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.cost = cost;
        this.availableSeats = availableSeats;
    }
}