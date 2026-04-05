package obuhov.airline.service;

import obuhov.airline.entity.BonusCard;
import obuhov.airline.entity.Client;
import obuhov.airline.entity.Airline;
import obuhov.airline.entity.Traveled;
import obuhov.airline.repository.BonusCardRepository;
import obuhov.airline.repository.TraveledRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = NONE)
public class BonusServiceTest {

    @Mock
    private BonusCardRepository bonusCardRepository;

    @Mock
    private TraveledRepository traveledRepository;

    @InjectMocks
    private BonusService bonusService;

    private BonusCard testBonusCard;
    private Client testClient;
    private Airline testAirline;

    @BeforeEach
    public void setUp() {
        testClient = new Client(1, "Иванов Иван", "+7-900-111-22-33", "ivanov@mail.ru", "Москва");
        testAirline = new Airline(1, "Аэрофлот");

        testBonusCard = new BonusCard();
        testBonusCard.setCardID(1);
        testBonusCard.setClient(testClient);
        testBonusCard.setAirline(testAirline);
        testBonusCard.setDiscount(10);
    }

    @Test
    @DisplayName("Получение всех бонусных карт")
    public void testGetAllBonusCards() {
        List<BonusCard> cards = Arrays.asList(testBonusCard);
        when(bonusCardRepository.findAll()).thenReturn(cards);

        List<BonusCard> result = bonusService.getAllBonusCards();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Получение бонусных карт по клиенту")
    public void testGetBonusCardsByClientId() {
        List<BonusCard> cards = Arrays.asList(testBonusCard);
        when(bonusCardRepository.findByClientId(1)).thenReturn(cards);

        List<BonusCard> result = bonusService.getBonusCardsByClientId(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Получение бонусной карты по клиенту и авиакомпании")
    public void testGetBonusCardByClientAndAirline() {
        when(bonusCardRepository.findByClientIdAndAirlineId(1, 1)).thenReturn(Optional.of(testBonusCard));

        Optional<BonusCard> result = bonusService.getBonusCardByClientAndAirline(1, 1);

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getDiscount());
    }

    @Test
    @DisplayName("Создание бонусной карты")
    public void testCreateBonusCard() {
        when(bonusCardRepository.save(any(BonusCard.class))).thenReturn(testBonusCard);

        BonusCard result = bonusService.createBonusCard(testBonusCard);

        assertNotNull(result);
        assertEquals(10, result.getDiscount());
        verify(bonusCardRepository, times(1)).save(any(BonusCard.class));
    }

    @Test
    @DisplayName("Создание бонусной карты с некорректной скидкой - исключение")
    public void testCreateBonusCardInvalidDiscount() {
        BonusCard invalidCard = new BonusCard();
        invalidCard.setClient(testClient);
        invalidCard.setAirline(testAirline);
        invalidCard.setDiscount(150);

        assertThrows(IllegalArgumentException.class, () -> {
            bonusService.createBonusCard(invalidCard);
        });
    }

    @Test
    @DisplayName("Получение общего расстояния клиента")
    public void testGetTotalDistanceByClientId() {
        when(traveledRepository.getTotalDistanceByClientId(1)).thenReturn(5000);

        Integer result = bonusService.getTotalDistanceByClientId(1);

        assertNotNull(result);
        assertEquals(5000, result);
    }

    @Test
    @DisplayName("Получение расстояния когда записей нет")
    public void testGetTotalDistanceNoRecords() {
        when(traveledRepository.getTotalDistanceByClientId(1)).thenReturn(null);

        Integer result = bonusService.getTotalDistanceByClientId(1);

        assertNotNull(result);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Добавление записи о налете")
    public void testAddTraveledRecord() {
        Traveled traveled = new Traveled();
        traveled.setClient(testClient);
        traveled.setAirline(testAirline);
        traveled.setDistance(2500);

        when(traveledRepository.save(any(Traveled.class))).thenReturn(traveled);

        Traveled result = bonusService.addTraveledRecord(traveled);

        assertNotNull(result);
        assertEquals(2500, result.getDistance());
    }

    @Test
    @DisplayName("Добавление записи о налете с отрицательным расстоянием - исключение")
    public void testAddTraveledRecordInvalidDistance() {
        Traveled invalidTraveled = new Traveled();
        invalidTraveled.setClient(testClient);
        invalidTraveled.setAirline(testAirline);
        invalidTraveled.setDistance(-100);

        assertThrows(IllegalArgumentException.class, () -> {
            bonusService.addTraveledRecord(invalidTraveled);
        });
    }

    @Test
    @DisplayName("Удаление существующей бонусной карты по ID")
    public void testDeleteExistingBonusCard() {
        Integer cardId = 1;
        when(bonusCardRepository.existsById(cardId)).thenReturn(true);
        bonusService.deleteBonusCard(cardId);
        verify(bonusCardRepository, times(1)).deleteById(cardId);
    }
}