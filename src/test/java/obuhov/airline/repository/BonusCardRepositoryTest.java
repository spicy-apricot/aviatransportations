package obuhov.airline.repository;

import obuhov.airline.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class BonusCardRepositoryTest {

    @Autowired private BonusCardRepository bonusCardRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private AirlineRepository airlineRepository;

    private Client client;
    private Airline airline;

    @BeforeEach
    void setUp() {
        client = clientRepository.save(new Client(null, "Иван", "+7-900", "ivan@test.ru", "Москва"));
        airline = airlineRepository.save(new Airline(null, "Аэрофлот"));

        BonusCard card = new BonusCard();
        card.setClient(client);
        card.setAirline(airline);
        card.setDiscount(15);
        bonusCardRepository.save(card);
    }

    @AfterEach
    void tearDown() {
        bonusCardRepository.deleteAll();
        clientRepository.deleteAll();
        airlineRepository.deleteAll();
    }

    @Test
    void repositoryQueries_returnExpectedResults() {
        assertEquals(1, bonusCardRepository.findByClientId(client.getClientID()).size());
        assertEquals(1, bonusCardRepository.findByAirlineId(airline.getAirlineID()).size());

        Optional<BonusCard> card = bonusCardRepository.findByClientIdAndAirlineId(client.getClientID(), airline.getAirlineID());
        assertTrue(card.isPresent());
        assertEquals(15, card.get().getDiscount());

        List<BonusCard> duplicateQuery = bonusCardRepository.findClientBonusCards(client.getClientID());
        assertEquals(1, duplicateQuery.size());
    }
}
