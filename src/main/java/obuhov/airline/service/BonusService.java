package obuhov.airline.service;

import obuhov.airline.entity.BonusCard;
import obuhov.airline.entity.Traveled;
import obuhov.airline.repository.BonusCardRepository;
import obuhov.airline.repository.TraveledRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BonusService {

    @Autowired
    private BonusCardRepository bonusCardRepository;

    @Autowired
    private TraveledRepository traveledRepository;

    public List<BonusCard> getAllBonusCards() {
        return bonusCardRepository.findAll();
    }

    public Optional<BonusCard> getBonusCardById(Integer id) {
        return bonusCardRepository.findById(id);
    }

    public List<BonusCard> getBonusCardsByClientId(Integer clientId) {
        return bonusCardRepository.findByClientId(clientId);
    }

    public List<BonusCard> getBonusCardsByAirlineId(Integer airlineId) {
        return bonusCardRepository.findByAirlineId(airlineId);
    }

    public Optional<BonusCard> getBonusCardByClientAndAirline(Integer clientId, Integer airlineId) {
        return bonusCardRepository.findByClientIdAndAirlineId(clientId, airlineId);
    }

    public BonusCard createBonusCard(BonusCard bonusCard) {
        validateBonusCard(bonusCard);
        return bonusCardRepository.save(bonusCard);
    }

    public BonusCard updateBonusCard(Integer id, BonusCard bonusCardDetails) {
        BonusCard bonusCard = bonusCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus card not found with id: " + id));

        bonusCard.setDiscount(bonusCardDetails.getDiscount());

        return bonusCardRepository.save(bonusCard);
    }

    public void deleteBonusCard(Integer id) {
        if (!bonusCardRepository.existsById(id)) {
            throw new RuntimeException("Bonus card not found with id: " + id);
        }
        bonusCardRepository.deleteById(id);
    }

    public List<Traveled> getTraveledRecordsByClientId(Integer clientId) {
        return traveledRepository.findByClientId(clientId);
    }

    public Integer getTotalDistanceByClientId(Integer clientId) {
        Integer total = traveledRepository.getTotalDistanceByClientId(clientId);
        return total != null ? total : 0;
    }

    public Integer getDistanceByClientAndAirline(Integer clientId, Integer airlineId) {
        Integer distance = traveledRepository.getDistanceByClientAndAirline(clientId, airlineId);
        return distance != null ? distance : 0;
    }

    public Traveled addTraveledRecord(Traveled traveled) {
        validateTraveled(traveled);
        return traveledRepository.save(traveled);
    }

    private void validateBonusCard(BonusCard bonusCard) {
        if (bonusCard.getClient() == null) {
            throw new IllegalArgumentException("Client is required");
        }
        if (bonusCard.getAirline() == null) {
            throw new IllegalArgumentException("Airline is required");
        }
        if (bonusCard.getDiscount() == null || bonusCard.getDiscount() < 0 || bonusCard.getDiscount() > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
    }

    private void validateTraveled(Traveled traveled) {
        if (traveled.getClient() == null) {
            throw new IllegalArgumentException("Client is required");
        }
        if (traveled.getAirline() == null) {
            throw new IllegalArgumentException("Airline is required");
        }
        if (traveled.getDistance() == null || traveled.getDistance() <= 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }
    }
}