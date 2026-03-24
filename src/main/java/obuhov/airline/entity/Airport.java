package obuhov.airline.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Airport")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AirportID")
    private Integer airportID;

    @Column(name = "Name", length = 300)
    private String name;

    @Column(name = "City", length = 300)
    private String city;
}