package obuhov.airline.repository;

import obuhov.airline.entity.Traveled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TraveledRepository extends JpaRepository<Traveled, Integer> {
    @Query("SELECT t FROM Traveled t WHERE t.client.clientID = :clientId")
    List<Traveled> findByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT t FROM Traveled t WHERE t.airline.airlineID = :airlineId")
    List<Traveled> findByAirlineId(@Param("airlineId") Integer airlineId);

    @Query("SELECT SUM(t.distance) FROM Traveled t WHERE t.client.clientID = :clientId")
    Integer getTotalDistanceByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT SUM(t.distance) FROM Traveled t WHERE t.client.clientID = :clientId AND t.airline.airlineID = :airlineId")
    Integer getDistanceByClientAndAirline(@Param("clientId") Integer clientId, @Param("airlineId") Integer airlineId);

    @Query("SELECT t FROM Traveled t WHERE t.client.clientID = :clientId ORDER BY t.distance DESC")
    List<Traveled> findByClientIdOrderByDistanceDesc(@Param("clientId") Integer clientId);
}