package obuhov.airline.service;

import obuhov.airline.entity.*;
import obuhov.airline.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceAdditionalTest {

    @Mock private ClientRepository clientRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private BonusCardRepository bonusCardRepository;
    @Mock private TraveledRepository traveledRepository;
    @InjectMocks private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client(1, "Иван", "+7-900", "ivan@test.ru", "Москва");
    }

    @Test
    void searchClients_normalizesNulls() {
        when(clientRepository.searchByNameOrPhone("", "")).thenReturn(List.of(client));
        List<Client> result = clientService.searchClients(null, null);
        assertEquals(1, result.size());
    }

    @Test
    void gettersByForeignKeys_coverRepositoryDelegation() {
        when(clientRepository.findByFlightId(10)).thenReturn(List.of(client));
        when(clientRepository.findByAirlineId(20)).thenReturn(List.of(client));
        when(ticketRepository.findByClientId(1)).thenReturn(List.of(new Ticket()));
        when(ticketRepository.findPaidTicketsByClientId(1)).thenReturn(List.of(new Ticket()));
        when(ticketRepository.findReservedTicketsByClientId(1)).thenReturn(List.of(new Ticket()));
        when(bonusCardRepository.findByClientId(1)).thenReturn(List.of(new BonusCard()));
        when(traveledRepository.findByClientId(1)).thenReturn(List.of(new Traveled()));
        when(traveledRepository.getTotalDistanceByClientId(1)).thenReturn(1500);

        assertEquals(1, clientService.getClientsByFlightId(10).size());
        assertEquals(1, clientService.getClientsByAirlineId(20).size());
        assertEquals(1, clientService.getClientTickets(1).size());
        assertEquals(1, clientService.getClientPaidTickets(1).size());
        assertEquals(1, clientService.getClientReservedTickets(1).size());
        assertEquals(1, clientService.getClientBonusCards(1).size());
        assertEquals(1, clientService.getClientTraveledRecords(1).size());
        assertEquals(1500, clientService.getClientTotalDistance(1));
    }

    @Test
    void getClientTotalDistance_returnsZeroWhenRepositoryReturnsNull() {
        when(traveledRepository.getTotalDistanceByClientId(1)).thenReturn(null);
        assertEquals(0, clientService.getClientTotalDistance(1));
    }

    @Test
    void updateClient_throwsWhenNotFound() {
        when(clientRepository.findById(99)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clientService.updateClient(99, client));
        assertTrue(ex.getMessage().contains("Client not found"));
    }

    @Test
    void updateClient_throwsForInvalidPhone() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        Client invalid = new Client();
        invalid.setName("Иван");
        invalid.setPhoneNumber("");
        assertThrows(IllegalArgumentException.class, () -> clientService.updateClient(1, invalid));
    }
}
