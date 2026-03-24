package obuhov.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "Ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TicketID")
    private Integer ticketID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FlightID")
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClientID")
    private Client client;

    @Column(name = "Seat", length = 10)
    private String seat;

    @Column(name = "Is_paid")
    @Check(constraints = "Is_paid IN (0, 1)")
    private Integer isPaid;



    public boolean isPaid() {
        return isPaid != null && isPaid == 1;
    }

    public boolean isReserved() {
        return isPaid != null && isPaid == 0;
    }
}