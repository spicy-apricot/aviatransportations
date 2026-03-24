package obuhov.airline.repository;

import obuhov.airline.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {
    List<Airline> findByNameContaining(String name);
}