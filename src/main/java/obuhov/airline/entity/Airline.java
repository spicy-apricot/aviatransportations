package obuhov.airline.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Airline")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AirlineID")
    private Integer airlineID;

    @Column(name = "Name", length = 100)
    private String name;
}