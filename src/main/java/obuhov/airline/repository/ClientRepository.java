package obuhov.airline.repository;

import obuhov.airline.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    List<Client> findByNameContaining(String name);

    List<Client> findByPhoneNumberContaining(String phoneNumber);

    @Query("SELECT DISTINCT c FROM Client c JOIN c.tickets t WHERE t.flight.flightID = :flightId")
    List<Client> findByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT DISTINCT c FROM Client c JOIN c.tickets t WHERE t.flight.airline.airlineID = :airlineId")
    List<Client> findByAirlineId(@Param("airlineId") Integer airlineId);

    @Query("SELECT c FROM Client c WHERE c.name LIKE %:name% OR c.phoneNumber LIKE %:phone%")
    List<Client> searchByNameOrPhone(@Param("name") String name, @Param("phone") String phone);
}