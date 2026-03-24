package obuhov.airline.repository;

import obuhov.airline.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query("SELECT t FROM Ticket t WHERE t.client.clientID = :clientId")
    List<Ticket> findByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT t FROM Ticket t WHERE t.flight.flightID = :flightId")
    List<Ticket> findByFlightId(@Param("flightId") Integer flightId);

    List<Ticket> findByIsPaid(Integer isPaid);

    @Query("SELECT t FROM Ticket t WHERE t.client.clientID = :clientId AND t.isPaid = 1")
    List<Ticket> findPaidTicketsByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT t FROM Ticket t WHERE t.client.clientID = :clientId AND t.isPaid = 0")
    List<Ticket> findReservedTicketsByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT t FROM Ticket t WHERE t.flight.flightID = :flightId AND t.seat = :seat")
    Ticket findByFlightIdAndSeat(@Param("flightId") Integer flightId, @Param("seat") String seat);

    // Исправлено: existsBy тоже требует правильного пути к свойствам
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Ticket t WHERE t.flight.flightID = :flightId AND t.seat = :seat")
    boolean existsByFlightIdAndSeat(@Param("flightId") Integer flightId, @Param("seat") String seat);
}