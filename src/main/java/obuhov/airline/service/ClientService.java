package obuhov.airline.service;

import obuhov.airline.entity.Client;
import obuhov.airline.entity.Ticket;
import obuhov.airline.entity.BonusCard;
import obuhov.airline.entity.Traveled;
import obuhov.airline.repository.ClientRepository;
import obuhov.airline.repository.TicketRepository;
import obuhov.airline.repository.BonusCardRepository;
import obuhov.airline.repository.TraveledRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BonusCardRepository bonusCardRepository;

    @Autowired
    private TraveledRepository traveledRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Integer id) {
        return clientRepository.findById(id);
    }

    public List<Client> searchClients(String name, String phone) {
        if (name == null || name.isEmpty()) name = "";
        if (phone == null || phone.isEmpty()) phone = "";
        return clientRepository.searchByNameOrPhone(name, phone);
    }

    public List<Client> getClientsByFlightId(Integer flightId) {
        return clientRepository.findByFlightId(flightId);
    }

    public List<Client> getClientsByAirlineId(Integer airlineId) {
        return clientRepository.findByAirlineId(airlineId);
    }

    public Client createClient(Client client) {
        validateClient(client);
        return clientRepository.save(client);
    }

    public Client updateClient(Integer id, Client clientDetails) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + id));

        validateClient(clientDetails);
        client.setName(clientDetails.getName());
        client.setPhoneNumber(clientDetails.getPhoneNumber());
        client.setEmail(clientDetails.getEmail());
        client.setAddress(clientDetails.getAddress());

        return clientRepository.save(client);
    }

    public void deleteClient(Integer id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    public List<Ticket> getClientTickets(Integer clientId) {
        return ticketRepository.findByClientId(clientId);
    }

    public List<Ticket> getClientPaidTickets(Integer clientId) {
        return ticketRepository.findPaidTicketsByClientId(clientId);
    }

    public List<Ticket> getClientReservedTickets(Integer clientId) {
        return ticketRepository.findReservedTicketsByClientId(clientId);
    }

    public List<BonusCard> getClientBonusCards(Integer clientId) {
        return bonusCardRepository.findByClientId(clientId);
    }

    public List<Traveled> getClientTraveledRecords(Integer clientId) {
        return traveledRepository.findByClientId(clientId);
    }

    public Integer getClientTotalDistance(Integer clientId) {
        Integer total = traveledRepository.getTotalDistanceByClientId(clientId);
        return total != null ? total : 0;
    }

    private void validateClient(Client client) {
        if (client.getName() == null || client.getName().isEmpty()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (client.getPhoneNumber() == null || client.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
    }
}