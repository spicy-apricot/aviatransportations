package obuhov.airline.repository;

import obuhov.airline.entity.Client;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = NONE)
public class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client savedClient;

    @BeforeEach
    public void setUp() {
        Client client = new Client();
        client.setName("Тестовый Клиент");
        client.setPhoneNumber("+7-999-000-00-00");
        client.setEmail("test@example.com");
        client.setAddress("Москва, ул. Тестовая 1");
        savedClient = clientRepository.save(client);
    }

    @AfterEach
    public void tearDown() {
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("Создание клиента")
    public void testCreateClient() {
        assertNotNull(savedClient.getClientID());
        assertEquals("Тестовый Клиент", savedClient.getName());
    }

    @Test
    @DisplayName("Поиск клиента по ID")
    public void testFindById() {
        Optional<Client> found = clientRepository.findById(savedClient.getClientID());
        assertTrue(found.isPresent());
        assertEquals(savedClient.getName(), found.get().getName());
    }

    @Test
    @DisplayName("Поиск клиента по имени")
    public void testFindByNameContaining() {
        List<Client> found = clientRepository.findByNameContaining("Тестовый");
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }

    @Test
    @DisplayName("Поиск клиента по телефону")
    public void testFindByPhoneNumberContaining() {
        List<Client> found = clientRepository.findByPhoneNumberContaining("999");
        assertFalse(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск по имени или телефону")
    public void testSearchByNameOrPhone() {
        List<Client> found = clientRepository.searchByNameOrPhone("Тестовый", "");
        assertFalse(found.isEmpty());

        List<Client> foundByPhone = clientRepository.searchByNameOrPhone("", "999");
        assertFalse(foundByPhone.isEmpty());
    }

    @Test
    @DisplayName("Удаление клиента")
    public void testDeleteClient() {
        clientRepository.delete(savedClient);
        Optional<Client> found = clientRepository.findById(savedClient.getClientID());
        assertFalse(found.isPresent());
    }
}