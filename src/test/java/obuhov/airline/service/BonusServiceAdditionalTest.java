package obuhov.airline.service;

import obuhov.airline.entity.*;
import obuhov.airline.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BonusServiceAdditionalTest {

    @Mock private BonusCardRepository bonusCardRepository;
    @Mock private TraveledRepository traveledRepository;
    @InjectMocks private BonusService bonusService;

    private BonusCard card;
    private Client client;
    private Airline airline;

    @BeforeEach
    void setUp() {
        client = new Client(1, "Иван", "+7-900", "ivan@test.ru", "Москва");
        airline = new Airline(2, "Аэрофлот");
        card = new BonusCard();
        card.setCardID(3);
        card.setClient(client);
        card.setAirline(airline);
        card.setDiscount(10);
    }

    @Test
    void delegatingMethods_areCovered() {
        when(bonusCardRepository.findById(3)).thenReturn(Optional.of(card));
        when(bonusCardRepository.findByAirlineId(2)).thenReturn(List.of(card));
        when(traveledRepository.findByClientId(1)).thenReturn(List.of(new Traveled()));
        when(traveledRepository.getDistanceByClientAndAirline(1, 2)).thenReturn(900);

        assertTrue(bonusService.getBonusCardById(3).isPresent());
        assertEquals(1, bonusService.getBonusCardsByAirlineId(2).size());
        assertEquals(1, bonusService.getTraveledRecordsByClientId(1).size());
        assertEquals(900, bonusService.getDistanceByClientAndAirline(1, 2));
    }

    @Test
    void getDistanceByClientAndAirline_returnsZeroWhenNull() {
        when(traveledRepository.getDistanceByClientAndAirline(1, 2)).thenReturn(null);
        assertEquals(0, bonusService.getDistanceByClientAndAirline(1, 2));
    }

    @Test
    void updateBonusCard_successAndNotFound() {
        BonusCard details = new BonusCard();
        details.setDiscount(25);

        when(bonusCardRepository.findById(3)).thenReturn(Optional.of(card));
        when(bonusCardRepository.save(any(BonusCard.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BonusCard updated = bonusService.updateBonusCard(3, details);
        assertEquals(25, updated.getDiscount());

        when(bonusCardRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> bonusService.updateBonusCard(99, details));
    }

    @Test
    void deleteBonusCard_notFound() {
        when(bonusCardRepository.existsById(5)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> bonusService.deleteBonusCard(5));
    }

    @Test
    void bonusCardValidation_coversMissingClientAndAirline() {
        BonusCard missingClient = new BonusCard();
        missingClient.setAirline(airline);
        missingClient.setDiscount(10);
        assertThrows(IllegalArgumentException.class, () -> bonusService.createBonusCard(missingClient));

        BonusCard missingAirline = new BonusCard();
        missingAirline.setClient(client);
        missingAirline.setDiscount(10);
        assertThrows(IllegalArgumentException.class, () -> bonusService.createBonusCard(missingAirline));
    }

    @Test
    void traveledValidation_coversMissingClientAndAirline() {
        Traveled missingClient = new Traveled();
        missingClient.setAirline(airline);
        missingClient.setDistance(100);
        assertThrows(IllegalArgumentException.class, () -> bonusService.addTraveledRecord(missingClient));

        Traveled missingAirline = new Traveled();
        missingAirline.setClient(client);
        missingAirline.setDistance(100);
        assertThrows(IllegalArgumentException.class, () -> bonusService.addTraveledRecord(missingAirline));
    }
}
