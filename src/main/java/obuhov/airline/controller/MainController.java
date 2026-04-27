package obuhov.airline.controller;

import obuhov.airline.entity.*;
import obuhov.airline.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired private FlightService flightService;
    @Autowired private ClientService clientService;
    @Autowired private TicketService ticketService;
    @Autowired private BonusService bonusService;

    // === ГЛАВНАЯ СТРАНИЦА ===
    @GetMapping
    public String home(@RequestParam(required = false) String from,
                       @RequestParam(required = false) String to,
                       @RequestParam(required = false) LocalDate date,
                       Model model) {
        List<Flight> flights = flightService.getAllFlights();

        if (from != null && !from.isEmpty()) {
            flights = flights.stream()
                    .filter(f -> f.getDepartureAirport().getCity().toLowerCase().contains(from.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (to != null && !to.isEmpty()) {
            flights = flights.stream()
                    .filter(f -> f.getArrivalAirport().getCity().toLowerCase().contains(to.toLowerCase()))
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
                    .filter(c -> c.getFullName().toLowerCase().contains(search.toLowerCase()) ||
                            c.getPhone().contains(search))
                    .collect(Collectors.toList());
        }
        model.addAttribute("clients", clients);
        model.addAttribute("search", search);
        return "clients/list";
    }

    @GetMapping("/clients/{id}")
    public String viewClient(@PathVariable Integer id, Model model) {
        Client client = clientService.getClientById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        model.addAttribute("client", client);
        model.addAttribute("traveled", bonusService.getTraveledByClient(id));
        return "clients/view";
    }

    @GetMapping("/clients/new")
    public String newClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "clients/form";
    }

    @PostMapping("/clients")
    public String saveClient(@ModelAttribute Client client) {
        if (client.getId() == null) {
            clientService.createClient(client);
        } else {
            clientService.updateClient(client.getId(), client);
        }
        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}/edit")
    public String editClient(@PathVariable Integer id, Model model) {
        Client client = clientService.getClientById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
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
                            f.getDepartureAirport().getName().toLowerCase().contains(filter.toLowerCase()) ||
                            f.getArrivalAirport().getName().toLowerCase().contains(filter.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (airlineId != null) {
            flights = flights.stream()
                    .filter(f -> f.getAirline().getId().equals(airlineId))
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
        model.addAttribute("airlines", new ArrayList<>()); // Можно добавить сервис AirlineService
        return "flights/list";
    }

    @GetMapping("/flights/{id}")
    public String viewFlight(@PathVariable Integer id, Model model) {
        Flight flight = flightService.getFlightById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        model.addAttribute("flight", flight);
        model.addAttribute("availableSeats", flightService.getAvailableSeats(id));
        return "flights/view";
    }

    @GetMapping("/flights/new")
    public String newFlightForm(Model model) {
        model.addAttribute("flight", new Flight());
        return "flights/form";
    }

    @PostMapping("/flights")
    public String saveFlight(@ModelAttribute Flight flight) {
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
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        model.addAttribute("flight", flight);
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
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (!flightService.isSeatAvailable(id, seat)) {
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
                               Model model) {
        // Создаём временный объект для отображения на странице оплаты
        model.addAttribute("flightId", flightId);
        model.addAttribute("seat", seat);
        model.addAttribute("clientName", clientName);
        model.addAttribute("clientPhone", clientPhone);
        model.addAttribute("bonusCards", new ArrayList<>()); // bonusService.getCardsByClient()
        return "order/payment";
    }

    @PostMapping("/order/pay")
    public String processPayment(@RequestParam Integer flightId,
                                 @RequestParam String seat,
                                 @RequestParam String clientName,
                                 @RequestParam String clientPhone,
                                 @RequestParam(required = false) Integer bonusCardId,
                                 Model model) {
        try {
            Flight flight = flightService.getFlightById(flightId).get();
            String seats = flight.getAvailableSeats();
            String newSeats = Arrays.stream(seats.split(","))
                    .filter(s -> !s.trim().equals(seat))
                    .collect(Collectors.joining(","));
            flight.setAvailableSeats(newSeats);
            flightService.updateFlight(flightId, flight);

            model.addAttribute("orderId", UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            return "order/success";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка оплаты: " + e.getMessage());
            model.addAttribute("flightId", flightId);
            model.addAttribute("seat", seat);
            model.addAttribute("clientName", clientName);
            model.addAttribute("clientPhone", clientPhone);
            return "order/failure";
        }
    }
}