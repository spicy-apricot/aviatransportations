package obuhov.airline.controller;

import obuhov.airline.entity.Airline;
import obuhov.airline.entity.Airport;
import obuhov.airline.entity.Client;
import obuhov.airline.entity.Flight;
import obuhov.airline.repository.AirlineRepository;
import obuhov.airline.repository.AirportRepository;
import obuhov.airline.service.BonusService;
import obuhov.airline.service.ClientService;
import obuhov.airline.service.FlightService;
import obuhov.airline.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainControllerCoverageTest {

    @Mock private FlightService flightService;
    @Mock private ClientService clientService;
    @Mock private TicketService ticketService;
    @Mock private BonusService bonusService;
    @Mock private AirportRepository airportRepository;
    @Mock private AirlineRepository airlineRepository;

    private MainController controller;
    private Airport moscow;
    private Airport pulkovo;
    private Airport kazan;
    private Airline aeroflot;
    private Flight flight;

    @BeforeEach
    void setUp() {
        controller = new MainController();
        ReflectionTestUtils.setField(controller, "flightService", flightService);
        ReflectionTestUtils.setField(controller, "clientService", clientService);
        ReflectionTestUtils.setField(controller, "ticketService", ticketService);
        ReflectionTestUtils.setField(controller, "bonusService", bonusService);
        ReflectionTestUtils.setField(controller, "airportRepository", airportRepository);
        ReflectionTestUtils.setField(controller, "airlineRepository", airlineRepository);

        moscow = new Airport(1, "Шереметьево", "Москва");
        pulkovo = new Airport(2, "Пулково", "Санкт-Петербург");
        kazan = new Airport(3, "Казань", "Казань");
        aeroflot = new Airline(1, "Аэрофлот");
        flight = flight(1, moscow, pulkovo, aeroflot, 7000, "1A,1B", LocalDate.of(2026, 5, 1));
    }

    @Test
    void homeCoversFilteringBranches() {
        Flight nullDeparture = flight(2, null, pulkovo, aeroflot, 6000, "2A", LocalDate.of(2026, 5, 1));
        Flight wrongRoute = flight(3, kazan, moscow, aeroflot, 5000, "3A", LocalDate.of(2026, 5, 2));
        Flight nullArrival = flight(4, moscow, null, aeroflot, 5500, "4A", LocalDate.of(2026, 5, 1));
        Flight wrongArrival = flight(5, moscow, kazan, aeroflot, 5800, "5A", LocalDate.of(2026, 5, 1));
        when(flightService.getAllFlights()).thenReturn(List.of(flight, nullDeparture, wrongRoute, nullArrival, wrongArrival));

        Model model = new ExtendedModelMap();
        assertEquals("index", controller.home("моск", "санкт", LocalDate.of(2026, 5, 1), model));
        assertEquals(List.of(flight), model.asMap().get("flights"));

        Model emptyFilterModel = new ExtendedModelMap();
        assertEquals("index", controller.home("", "", null, emptyFilterModel));
        assertEquals(5, ((List<?>) emptyFilterModel.asMap().get("flights")).size());
    }

    @Test
    void clientPagesCoverSuccessNotFoundAndSaveBranches() {
        Client newClient = client(null, "Иван", "+70000000000");
        Client savedClient = client(1, "Анна", "+71111111111");
        when(clientService.getAllClients()).thenReturn(List.of(savedClient, client(2, null, null)));
        when(clientService.getClientById(1)).thenReturn(Optional.of(savedClient));
        when(clientService.getClientById(404)).thenReturn(Optional.empty());

        Model listModel = new ExtendedModelMap();
        assertEquals("clients/list", controller.listClients("+711", listModel));
        assertEquals(1, ((List<?>) listModel.asMap().get("clients")).size());

        Model nameSearchModel = new ExtendedModelMap();
        assertEquals("clients/list", controller.listClients("анн", nameSearchModel));
        assertEquals(1, ((List<?>) nameSearchModel.asMap().get("clients")).size());

        Model emptySearchModel = new ExtendedModelMap();
        assertEquals("clients/list", controller.listClients("", emptySearchModel));
        assertEquals(2, ((List<?>) emptySearchModel.asMap().get("clients")).size());

        Model plainListModel = new ExtendedModelMap();
        assertEquals("clients/list", controller.listClients(null, plainListModel));
        assertEquals(2, ((List<?>) plainListModel.asMap().get("clients")).size());

        Model viewModel = new ExtendedModelMap();
        when(bonusService.getTraveledRecordsByClientId(1)).thenReturn(List.of());
        assertEquals("clients/view", controller.viewClient(1, viewModel));
        assertSame(savedClient, viewModel.asMap().get("client"));
        assertThrows(ResponseStatusException.class, () -> controller.viewClient(404, new ExtendedModelMap()));

        Model formModel = new ExtendedModelMap();
        assertEquals("clients/form", controller.newClientForm(formModel));
        assertTrue(formModel.asMap().get("client") instanceof Client);

        assertEquals("redirect:/clients", controller.saveClient(newClient));
        verify(clientService).createClient(newClient);
        assertEquals("redirect:/clients", controller.saveClient(savedClient));
        verify(clientService).updateClient(1, savedClient);

        Model editModel = new ExtendedModelMap();
        assertEquals("clients/form", controller.editClient(1, editModel));
        assertThrows(ResponseStatusException.class, () -> controller.editClient(404, new ExtendedModelMap()));

        assertEquals("redirect:/clients", controller.deleteClient(1));
        verify(clientService).deleteClient(1);
    }

    @Test
    void flightPagesCoverFiltersFormsCrudAndNotFoundBranches() {
        Flight other = flight(2, kazan, moscow, new Airline(2, "S7"), 3000, "", LocalDate.of(2026, 5, 2));
        Flight noAirports = flight(12, null, null, aeroflot, 9000, "9A", LocalDate.of(2026, 5, 3));
        when(flightService.getAllFlights()).thenReturn(List.of(flight, other, noAirports));
        when(airlineRepository.findAll()).thenReturn(List.of(aeroflot));
        when(airportRepository.findAll()).thenReturn(List.of(moscow, pulkovo, kazan));
        when(flightService.getFlightById(1)).thenReturn(Optional.of(flight));
        when(flightService.getFlightById(404)).thenReturn(Optional.empty());
        when(flightService.getAvailableSeats(1)).thenReturn(List.of("1A", "1B"));

        Model listModel = new ExtendedModelMap();
        assertEquals("flights/list", controller.listFlights("1", 1, 6000, 8000, listModel));
        assertEquals(List.of(flight), listModel.asMap().get("flights"));

        Model routeFilterModel = new ExtendedModelMap();
        assertEquals("flights/list", controller.listFlights("каз", null, null, null, routeFilterModel));
        assertEquals(1, ((List<?>) routeFilterModel.asMap().get("flights")).size());

        Model arrivalFilterModel = new ExtendedModelMap();
        assertEquals("flights/list", controller.listFlights("пулк", null, null, null, arrivalFilterModel));
        assertEquals(List.of(flight), arrivalFilterModel.asMap().get("flights"));

        Model emptyFilterModel = new ExtendedModelMap();
        assertEquals("flights/list", controller.listFlights("", null, null, null, emptyFilterModel));
        assertEquals(3, ((List<?>) emptyFilterModel.asMap().get("flights")).size());

        Model unmatchedFilterModel = new ExtendedModelMap();
        assertEquals("flights/list", controller.listFlights("zzz", null, null, null, unmatchedFilterModel));
        assertTrue(((List<?>) unmatchedFilterModel.asMap().get("flights")).isEmpty());

        Model minCostFalseModel = new ExtendedModelMap();
        assertEquals("flights/list", controller.listFlights(null, null, 8000, null, minCostFalseModel));
        assertEquals(List.of(noAirports), minCostFalseModel.asMap().get("flights"));

        Model noFilterModel = new ExtendedModelMap();
        assertEquals("flights/list", controller.listFlights(null, null, null, null, noFilterModel));
        assertEquals(3, ((List<?>) noFilterModel.asMap().get("flights")).size());

        Model viewModel = new ExtendedModelMap();
        assertEquals("flights/view", controller.viewFlight(1, viewModel));
        assertThrows(ResponseStatusException.class, () -> controller.viewFlight(404, new ExtendedModelMap()));

        Model newFormModel = new ExtendedModelMap();
        assertEquals("flights/form", controller.newFlightForm(newFormModel));
        assertEquals(List.of(moscow, pulkovo, kazan), newFormModel.asMap().get("airports"));

        Model editFormModel = new ExtendedModelMap();
        assertEquals("flights/form", controller.editFlight(1, editFormModel));
        assertThrows(ResponseStatusException.class, () -> controller.editFlight(404, new ExtendedModelMap()));

        when(airportRepository.findById(1)).thenReturn(Optional.of(moscow));
        when(airportRepository.findById(2)).thenReturn(Optional.of(pulkovo));
        when(airportRepository.findById(404)).thenReturn(Optional.empty());
        when(airlineRepository.findById(1)).thenReturn(Optional.of(aeroflot));
        when(airlineRepository.findById(404)).thenReturn(Optional.empty());

        assertEquals("redirect:/flights", controller.saveFlight(null, 1, 2, 1,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), "10:00", "12:00", 7000, "1A"));
        verify(flightService).createFlight(any(Flight.class));

        assertEquals("redirect:/flights", controller.saveFlight(7, 1, 2, 1,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), "10:00", "12:00", 7000, "1A"));
        verify(flightService).updateFlight(eq(7), any(Flight.class));

        assertThrows(ResponseStatusException.class, () -> controller.saveFlight(null, 404, 2, 1,
                LocalDate.now(), LocalDate.now(), "10:00", "12:00", 1, ""));
        assertThrows(ResponseStatusException.class, () -> controller.saveFlight(null, 1, 404, 1,
                LocalDate.now(), LocalDate.now(), "10:00", "12:00", 1, ""));
        assertThrows(ResponseStatusException.class, () -> controller.saveFlight(null, 1, 2, 404,
                LocalDate.now(), LocalDate.now(), "10:00", "12:00", 1, ""));

        assertEquals("redirect:/flights", controller.deleteFlight(1));
        verify(flightService).deleteFlight(1);
    }

    @Test
    void ticketOrderPagesCoverSuccessAndFailureBranches() {
        when(flightService.getFlightById(1)).thenReturn(Optional.of(flight));
        when(flightService.getFlightById(404)).thenReturn(Optional.empty());
        when(flightService.isSeatAvailable(1, "1A")).thenReturn(true);
        when(flightService.isSeatAvailable(1, "1B")).thenReturn(false);

        Model checkoutModel = new ExtendedModelMap();
        assertEquals("order/checkout", controller.buyTicket(1, "1A", checkoutModel));
        assertSame(flight, checkoutModel.asMap().get("flight"));
        assertEquals("1A", checkoutModel.asMap().get("seat"));

        Model busySeatModel = new ExtendedModelMap();
        assertEquals("flights/view", controller.buyTicket(1, "1B", busySeatModel));
        assertTrue(busySeatModel.asMap().get("error").toString().contains("занято"));
        assertThrows(ResponseStatusException.class, () -> controller.buyTicket(404, "1A", new ExtendedModelMap()));

        Model confirmModel = new ExtendedModelMap();
        assertEquals("order/payment", controller.confirmOrder(1, "1A", "Иван", "+7000", confirmModel));
        assertEquals("Иван", confirmModel.asMap().get("clientName"));

        Model successModel = new ExtendedModelMap();
        assertEquals("order/success", controller.processPayment(1, "1A", "Иван", "+7000", null, successModel));
        assertNotNull(successModel.asMap().get("orderId"));
        verify(flightService).updateFlight(eq(1), any(Flight.class));

        when(flightService.getFlightById(500)).thenReturn(Optional.empty());
        Model failureModel = new ExtendedModelMap();
        assertEquals("order/failure", controller.processPayment(500, "9A", "Иван", "+7000", null, failureModel));
        assertTrue(failureModel.asMap().get("error").toString().contains("Ошибка оплаты"));

        Model successPageModel = new ExtendedModelMap();
        assertEquals("order/success", controller.successPage(successPageModel));
        assertEquals("TEST", successPageModel.asMap().get("orderId"));

        Model failurePageModel = new ExtendedModelMap();
        assertEquals("order/failure", controller.failurePage(failurePageModel));
        assertEquals("Платеж не был выполнен", failurePageModel.asMap().get("error"));
    }

    @Test
    void privateContainsIgnoreCaseCoversNullAndFalseBranches() {
        assertEquals(Boolean.FALSE, ReflectionTestUtils.invokeMethod(controller, "containsIgnoreCase", null, "x"));
        assertEquals(Boolean.FALSE, ReflectionTestUtils.invokeMethod(controller, "containsIgnoreCase", "abc", null));
        assertEquals(Boolean.FALSE, ReflectionTestUtils.invokeMethod(controller, "containsIgnoreCase", "abc", "z"));
        assertEquals(Boolean.TRUE, ReflectionTestUtils.invokeMethod(controller, "containsIgnoreCase", "Abc", "a"));
    }

    private Client client(Integer id, String name, String phone) {
        return new Client(id, name, phone, "mail@example.com", "address");
    }

    private Flight flight(Integer id, Airport departure, Airport arrival, Airline airline,
                          Integer cost, String seats, LocalDate date) {
        Flight result = new Flight();
        result.setFlightID(id);
        result.setDepartureAirport(departure);
        result.setArrivalAirport(arrival);
        result.setAirline(airline);
        result.setDepartureDate(date);
        result.setArrivalDate(date);
        result.setDepartureTime(LocalTime.of(10, 0));
        result.setArrivalTime(LocalTime.of(12, 0));
        result.setCost(cost);
        result.setAvailableSeats(seats);
        return result;
    }
}
