package obuhov.airline.repository;

import obuhov.airline.entity.Airport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class AirportRepositoryTest {

    @Autowired
    private AirportRepository airportRepository;

    @BeforeEach
    void setUp() {
        airportRepository.save(new Airport(null, "Шереметьево", "Москва"));
        airportRepository.save(new Airport(null, "Домодедово", "Москва"));
        airportRepository.save(new Airport(null, "Пулково", "Санкт-Петербург"));
    }

    @AfterEach
    void tearDown() {
        airportRepository.deleteAll();
    }

    @Test
    void findByCity_returnsAllAirportsInCity() {
        List<Airport> result = airportRepository.findByCity("Москва");
        assertEquals(3, result.size());
    }

    @Test
    void findByCityOrNameContaining_worksForCityAndName() {
        List<Airport> byCity = airportRepository.findByCityOrNameContaining("Москва", "zzz");
        assertEquals(3, byCity.size());

        List<Airport> byName = airportRepository.findByCityOrNameContaining("Неизвестно", "Пулк");
        assertEquals(2, byName.size());
        assertEquals("Пулково", byName.get(0).getName());
    }
}
