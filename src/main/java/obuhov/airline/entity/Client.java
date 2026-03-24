package obuhov.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ClientID")
    private Integer clientID;

    @Column(name = "Name", length = 100)
    private String name;

    @Column(name = "Phone_number", length = 100)
    private String phoneNumber;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "Address", length = 100)
    private String address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BonusCard> bonusCards;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Traveled> traveledRecords;

    public Client(Integer clientID, String name, String phoneNumber, String email, String address) {
        this.clientID = clientID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }
}