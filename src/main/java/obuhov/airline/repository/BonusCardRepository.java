package obuhov.airline.repository;

import obuhov.airline.entity.BonusCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BonusCardRepository extends JpaRepository<BonusCard, Integer> {
    @Query("SELECT bc FROM BonusCard bc WHERE bc.client.clientID = :clientId")
    List<BonusCard> findByClientId(@Param("clientId") Integer clientId);

    @Query("SELECT bc FROM BonusCard bc WHERE bc.airline.airlineID = :airlineId")
    List<BonusCard> findByAirlineId(@Param("airlineId") Integer airlineId);

    @Query("SELECT bc FROM BonusCard bc WHERE bc.client.clientID = :clientId AND bc.airline.airlineID = :airlineId")
    Optional<BonusCard> findByClientIdAndAirlineId(@Param("clientId") Integer clientId, @Param("airlineId") Integer airlineId);

    @Query("SELECT bc FROM BonusCard bc WHERE bc.client.clientID = :clientId")
    List<BonusCard> findClientBonusCards(@Param("clientId") Integer clientId);
}