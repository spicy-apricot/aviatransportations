package obuhov.airline.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Bonus_Card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BonusCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CardID")
    private Integer cardID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AirlineID")
    private Airline airline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClientID")
    private Client client;

    @Column(name = "Discount")
    private Integer discount;


}