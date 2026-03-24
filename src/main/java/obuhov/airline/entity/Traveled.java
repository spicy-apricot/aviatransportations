package obuhov.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "Traveled")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Traveled {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecordID")
    private Integer recordID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClientID")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AirlineID")
    private Airline airline;

    @Column(name = "Distance")
    @Check(constraints = "Distance > 0")
    private Integer distance;


}