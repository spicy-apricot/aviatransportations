package obuhov.airline.repository;

import obuhov.airline.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    List<Flight> findByDepartureDate(LocalDate date);

    List<Flight> findByArrivalDate(LocalDate date);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport.city = :fromCity AND f.arrivalAirport.city = :toCity")
    List<Flight> findByRoute(@Param("fromCity") String fromCity, @Param("toCity") String toCity);

    @Query("SELECT f FROM Flight f WHERE f.departureDate = :date AND f.departureAirport.city = :fromCity")
    List<Flight> findByDateAndFromCity(@Param("date") LocalDate date, @Param("fromCity") String fromCity);

    @Query("SELECT f FROM Flight f WHERE f.airline.airlineID = :airlineId")
    List<Flight> findByAirlineId(@Param("airlineId") Integer airlineId);

    @Query("SELECT f FROM Flight f WHERE f.cost BETWEEN :minCost AND :maxCost")
    List<Flight> findByCostRange(@Param("minCost") Integer minCost, @Param("maxCost") Integer maxCost);

    @Query("SELECT f FROM Flight f WHERE LENGTH(f.availableSeats) > 0")
    List<Flight> findFlightsWithAvailableSeats();
}