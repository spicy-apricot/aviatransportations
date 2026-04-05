package obuhov.airline.repository;

import obuhov.airline.entity.Airline;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class AirlineRepositoryTest {

    @Autowired
    private AirlineRepository airlineRepository;

    @BeforeEach
    void setUp() {
        airlineRepository.save(new Airline(null, "Аэрофлот"));
        airlineRepository.save(new Airline(null, "S7 Airlines"));
    }

    @AfterEach
    void tearDown() {
        airlineRepository.deleteAll();
    }

    @Test
    void findByNameContaining_returnsMatches() {
        List<Airline> result = airlineRepository.findByNameContaining("Air");
        assertEquals(2, result.size());
        assertEquals("S7 Airlines", result.get(0).getName());
    }
}
