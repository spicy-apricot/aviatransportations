package obuhov.airline.repository;

import obuhov.airline.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Integer> {
    List<Airport> findByCity(String city);

    @Query("SELECT a FROM Airport a WHERE a.city = :city OR a.name LIKE %:name%")
    List<Airport> findByCityOrNameContaining(@Param("city") String city, @Param("name") String name);
}