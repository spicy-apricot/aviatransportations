package obuhov.airline.service;

import obuhov.airline.entity.Client;
import obuhov.airline.repository.ClientRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = NONE)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;

    @BeforeEach
    public void setUp() {
        testClient = new Client(1, "Иванов Иван", "+7-900-111-22-33", "ivanov@mail.ru", "Москва");
    }

    @Test
    @DisplayName("Получение всех клиентов")
    public void testGetAllClients() {
        List<Client> clients = Arrays.asList(testClient);
        when(clientRepository.findAll()).thenReturn(clients);

        List<Client> result = clientService.getAllClients();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Получение клиента по ID")
    public void testGetClientById() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));

        Optional<Client> result = clientService.getClientById(1);

        assertTrue(result.isPresent());
        assertEquals("Иванов Иван", result.get().getName());
    }

    @Test
    @DisplayName("Клиент не найден по ID")
    public void testGetClientByIdNotFound() {
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.getClientById(999);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Создание клиента")
    public void testCreateClient() {
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        Client result = clientService.createClient(testClient);

        assertNotNull(result);
        assertEquals("Иванов Иван", result.getName());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Создание клиента без имени - исключение")
    public void testCreateClientWithoutName() {
        Client invalidClient = new Client();
        invalidClient.setPhoneNumber("+7-900-111-22-33");

        assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(invalidClient);
        });
    }

    @Test
    @DisplayName("Обновление клиента")
    public void testUpdateClient() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        Client updatedClient = new Client();
        updatedClient.setName("Иванов Иван Обновленный");
        updatedClient.setPhoneNumber("+7-900-111-22-33");

        Client result = clientService.updateClient(1, updatedClient);

        assertNotNull(result);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Удаление клиента")
    public void testDeleteClient() {
        when(clientRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> clientService.deleteClient(1));
        verify(clientRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Удаление несуществующего клиента - исключение")
    public void testDeleteClientNotFound() {
        when(clientRepository.existsById(999)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            clientService.deleteClient(999);
        });
    }

    @Test
    @DisplayName("Поиск клиентов по имени и телефону")
    public void testSearchClients() {
        List<Client> clients = Arrays.asList(testClient);
        when(clientRepository.searchByNameOrPhone("Иванов", "")).thenReturn(clients);

        List<Client> result = clientService.searchClients("Иванов", "");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}