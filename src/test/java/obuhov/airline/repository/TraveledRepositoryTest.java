package obuhov.airline.repository;

import obuhov.airline.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class TraveledRepositoryTest {

    @Autowired private TraveledRepository traveledRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private AirlineRepository airlineRepository;

    private Client client;
    private Airline airline;

    @BeforeEach
    void setUp() {
        client = clientRepository.save(new Client(null, "Иван", "+7-900", "ivan@test.ru", "Москва"));
        airline = airlineRepository.save(new Airline(null, "Аэрофлот"));

        Traveled first = new Traveled();
        first.setClient(client);
        first.setAirline(airline);
        first.setDistance(1200);
        traveledRepository.save(first);

        Traveled second = new Traveled();
        second.setClient(client);
        second.setAirline(airline);
        second.setDistance(2500);
        traveledRepository.save(second);
    }

    @AfterEach
    void tearDown() {
        traveledRepository.deleteAll();
        clientRepository.deleteAll();
        airlineRepository.deleteAll();
    }

    @Test
    void repositoryQueries_coverAllMethods() {
        assertEquals(2, traveledRepository.findByClientId(client.getClientID()).size());
        assertEquals(2, traveledRepository.findByAirlineId(airline.getAirlineID()).size());
        assertEquals(3700, traveledRepository.getTotalDistanceByClientId(client.getClientID()));
        assertEquals(3700, traveledRepository.getDistanceByClientAndAirline(client.getClientID(), airline.getAirlineID()));

        List<Traveled> sorted = traveledRepository.findByClientIdOrderByDistanceDesc(client.getClientID());
        assertEquals(2, sorted.size());
        assertEquals(2500, sorted.get(0).getDistance());
        assertEquals(1200, sorted.get(1).getDistance());
    }
}
