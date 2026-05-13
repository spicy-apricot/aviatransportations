package obuhov.airline.controller;

import obuhov.airline.entity.*;
import obuhov.airline.repository.AirlineRepository;
import obuhov.airline.repository.AirportRepository;
import obuhov.airline.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired private FlightService flightService;
    @Autowired private ClientService clientService;
    @Autowired private TicketService ticketService;
    @Autowired private BonusService bonusService;
    @Autowired private AirportRepository airportRepository;
    @Autowired private AirlineRepository airlineRepository;

    // === ГЛАВНАЯ СТРАНИЦА ===
    @GetMapping
    public String home(@RequestParam(required = false) String from,
                       @RequestParam(required = false) String to,
                       @RequestParam(required = false) LocalDate date,
                       Model model) {
        List<Flight> flights = flightService.getAllFlights();

        if (from != null && !from.isEmpty()) {
            flights = flights.stream()
                    .filter(f -> f.getDepartureAirport() != null && containsIgnoreCase(f.getDepartureAirport().getCity(), from))
                    .collect(Collectors.toList());
        }
        if (to != null && !to.isEmpty()) {
            flights = flights.stream()
                    .filter(f -> f.getArrivalAirport() != null && containsIgnoreCase(f.getArrivalAirport().getCity(), to))
                    .collect(Collectors.toList());
        }
        if (date != null) {
            flights = flights.stream()
                    .filter(f -> f.getDepartureDate().equals(date))
                    .collect(Collectors.toList());
        }

        model.addAttribute("flights", flights);
        model.addAttribute("searchFrom", from);
        model.addAttribute("searchTo", to);
        model.addAttribute("searchDate", date);
        return "index";
    }

    // === КЛИЕНТЫ ===
    @GetMapping("/clients")
    public String listClients(@RequestParam(required = false) String search, Model model) {
        List<Client> clients = clientService.getAllClients();
        if (search != null && !search.isEmpty()) {
            clients = clients.stream()
                    .filter(c -> containsIgnoreCase(c.getName(), search) || containsIgnoreCase(c.getPhoneNumber(), search))
                    .collect(Collectors.toList());
        }
        model.addAttribute("clients", clients);
        model.addAttribute("search", search);
        return "clients/list";
    }

    @GetMapping("/clients/{id}")
    public String viewClient(@PathVariable Integer id, Model model) {
        Client client = clientService.getClientById(id)
                .orElseThrow(() -> notFound("Client not found"));
        model.addAttribute("client", client);
        model.addAttribute("tickets", clientService.getClientTickets(id));
        model.addAttribute("traveled", bonusService.getTraveledRecordsByClientId(id));
        return "clients/view";
    }

    @GetMapping("/clients/new")
    public String newClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "clients/form";
    }

    @PostMapping("/clients")
    public String saveClient(@ModelAttribute Client client) {
        if (client.getClientID() == null) {
            clientService.createClient(client);
        } else {
            clientService.updateClient(client.getClientID(), client);
        }
        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}/edit")
    public String editClient(@PathVariable Integer id, Model model) {
        Client client = clientService.getClientById(id)
                .orElseThrow(() -> notFound("Client not found"));
        model.addAttribute("client", client);
        return "clients/form";
    }

    @PostMapping("/clients/{id}/delete")
    public String deleteClient(@PathVariable Integer id) {
        clientService.deleteClient(id);
        return "redirect:/clients";
    }

    // === РЕЙСЫ ===
    @GetMapping("/flights")
    public String listFlights(@RequestParam(required = false) String filter,
                              @RequestParam(required = false) Integer airlineId,
                              @RequestParam(required = false) Integer minCost,
                              @RequestParam(required = false) Integer maxCost,
                              Model model) {
        List<Flight> flights = flightService.getAllFlights();

        if (filter != null && !filter.isEmpty()) {
            flights = flights.stream()
                    .filter(f -> f.getFlightID().toString().contains(filter) ||
                            (f.getDepartureAirport() != null && containsIgnoreCase(f.getDepartureAirport().getName(), filter)) ||
                            (f.getArrivalAirport() != null && containsIgnoreCase(f.getArrivalAirport().getName(), filter)))
                    .collect(Collectors.toList());
        }
        if (airlineId != null) {
            flights = flights.stream()
                    .filter(f -> f.getAirline().getAirlineID().equals(airlineId))
                    .collect(Collectors.toList());
        }
        if (minCost != null) {
            flights = flights.stream()
                    .filter(f -> f.getCost() >= minCost)
                    .collect(Collectors.toList());
        }
        if (maxCost != null) {
            flights = flights.stream()
                    .filter(f -> f.getCost() <= maxCost)
                    .collect(Collectors.toList());
        }

        model.addAttribute("flights", flights);
        model.addAttribute("airlines", airlineRepository.findAll());
        model.addAttribute("filter", filter);
        model.addAttribute("airlineId", airlineId);
        model.addAttribute("minCost", minCost);
        model.addAttribute("maxCost", maxCost);
        return "flights/list";
    }

    @GetMapping("/flights/{id}")
    public String viewFlight(@PathVariable Integer id, Model model) {
        Flight flight = flightService.getFlightById(id)
                .orElseThrow(() -> notFound("Flight not found"));
        model.addAttribute("flight", flight);
        model.addAttribute("availableSeats", flightService.getAvailableSeats(id));
        return "flights/view";
    }

    @GetMapping("/flights/new")
    public String newFlightForm(Model model) {
        model.addAttribute("flight", new Flight());
        addFlightDictionaries(model);
        return "flights/form";
    }

    @PostMapping("/flights")
    public String saveFlight(@RequestParam(required = false) Integer flightID,
                             @RequestParam Integer departureAirportId,
                             @RequestParam Integer arrivalAirportId,
                             @RequestParam Integer airlineId,
                             @RequestParam LocalDate departureDate,
                             @RequestParam LocalDate arrivalDate,
                             @RequestParam String departureTime,
                             @RequestParam String arrivalTime,
                             @RequestParam Integer cost,
                             @RequestParam(required = false) String availableSeats) {
        Flight flight = new Flight();
        flight.setFlightID(flightID);
        flight.setDepartureAirport(airportRepository.findById(departureAirportId).orElseThrow(() -> notFound("Departure airport not found")));
        flight.setArrivalAirport(airportRepository.findById(arrivalAirportId).orElseThrow(() -> notFound("Arrival airport not found")));
        flight.setAirline(airlineRepository.findById(airlineId).orElseThrow(() -> notFound("Airline not found")));
        flight.setDepartureDate(departureDate);
        flight.setArrivalDate(arrivalDate);
        flight.setDepartureTime(LocalTime.parse(departureTime));
        flight.setArrivalTime(LocalTime.parse(arrivalTime));
        flight.setCost(cost);
        flight.setAvailableSeats(availableSeats);

        if (flight.getFlightID() == null) {
            flightService.createFlight(flight);
        } else {
            flightService.updateFlight(flight.getFlightID(), flight);
        }
        return "redirect:/flights";
    }

    @GetMapping("/flights/{id}/edit")
    public String editFlight(@PathVariable Integer id, Model model) {
        Flight flight = flightService.getFlightById(id)
                .orElseThrow(() -> notFound("Flight not found"));
        model.addAttribute("flight", flight);
        addFlightDictionaries(model);
        return "flights/form";
    }

    @PostMapping("/flights/{id}/delete")
    public String deleteFlight(@PathVariable Integer id) {
        flightService.deleteFlight(id);
        return "redirect:/flights";
    }

    // === ЗАКАЗ БИЛЕТА ===
    @GetMapping("/flights/{id}/buy")
    public String buyTicket(@PathVariable Integer id, @RequestParam String seat, Model model) {
        Flight flight = flightService.getFlightById(id)
                .orElseThrow(() -> notFound("Flight not found"));

        if (!flightService.isSeatAvailable(id, seat)) {
            model.addAttribute("flight", flight);
            model.addAttribute("availableSeats", flightService.getAvailableSeats(id));
            model.addAttribute("error", "Место " + seat + " уже занято");
            return "flights/view";
        }

        model.addAttribute("flight", flight);
        model.addAttribute("seat", seat);
        model.addAttribute("ticket", new Ticket());
        return "order/checkout";
    }

    @PostMapping("/order/confirm")
    public String confirmOrder(@RequestParam Integer flightId,
                               @RequestParam String seat,
                               @RequestParam String clientName,
                               @RequestParam String clientPhone,
                               @RequestParam(required = false) Integer flightCost,
                               Model model) {
        // Создаём временный объект для отображения на странице оплаты
        model.addAttribute("flightId", flightId);
        model.addAttribute("seat", seat);
        model.addAttribute("clientName", clientName);
        model.addAttribute("clientPhone", clientPhone);
        model.addAttribute("flightCost", flightCost);
        model.addAttribute("bonusCards", new ArrayList<>()); // bonusService.getCardsByClient()
        return "order/payment";
    }

    @PostMapping("/order/pay")
    public String processPayment(@RequestParam Integer flightId,
                                 @RequestParam String seat,
                                 @RequestParam String clientName,
                                 @RequestParam String clientPhone,
                                 @RequestParam(required = false) Integer flightCost,
                                 @RequestParam(required = false) Integer bonusCardId,
                                 Model model) {
        try {
            Flight flight = flightService.getFlightById(flightId)
                    .orElseThrow(() -> new RuntimeException("Flight not found"));
            Client client = clientService.findByNameAndPhoneNumber(clientName, clientPhone)
                    .orElseGet(() -> clientService.createClient(new Client(null, clientName, clientPhone, null, null)));
            Ticket bookedTicket = ticketService.bookTicket(flightId, client.getClientID(), seat);
            ticketService.payTicket(bookedTicket.getTicketID());

            String seats = flight.getAvailableSeats() == null ? "" : flight.getAvailableSeats();
            String newSeats = Arrays.stream(seats.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && !s.equals(seat))
                    .collect(Collectors.joining(","));
            flight.setAvailableSeats(newSeats);
            flightService.updateFlight(flightId, flight);

            model.addAttribute("orderId", UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            model.addAttribute("flightCost", flightCost);
            return "order/success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка оплаты: " + e.getMessage());
            model.addAttribute("flightId", flightId);
            model.addAttribute("seat", seat);
            model.addAttribute("clientName", clientName);
            model.addAttribute("clientPhone", clientPhone);
            model.addAttribute("flightCost", flightCost);
            return "order/failure";
        }
    }

    @GetMapping("/order/success")
    public String successPage(Model model) {
        model.addAttribute("orderId", "TEST");
        return "order/success";
    }

    @GetMapping("/order/failure")
    public String failurePage(Model model) {
        model.addAttribute("error", "Платеж не был выполнен");
        return "order/failure";
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && search != null && value.toLowerCase().contains(search.toLowerCase());
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private void addFlightDictionaries(Model model) {
        model.addAttribute("airports", airportRepository.findAll());
        model.addAttribute("airlines", airlineRepository.findAll());
    }
}
