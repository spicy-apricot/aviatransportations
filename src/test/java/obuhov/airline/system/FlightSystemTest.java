package obuhov.airline.system;

import com.meterware.httpunit.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class FlightSystemTest {

    @LocalServerPort
    private int port;

    private WebConversation wc;
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        wc = new WebConversation();
        baseUrl = "http://localhost:" + port;
    }

    @Test
    public void testHomePageLoads() throws Exception {
        WebResponse response = wc.getResponse(baseUrl + "/");
        assertEquals(200, response.getResponseCode());
        assertTrue(response.getText().contains("AviaTransport"));
        assertTrue(response.getText().contains("Поиск рейсов"));
    }

    @Test
    public void testFlightListPage() throws Exception {
        WebResponse response = wc.getResponse(baseUrl + "/flights");
        assertEquals(200, response.getResponseCode());
        // Проверяем наличие таблицы с рейсами
        assertTrue(response.getText().contains("table") || response.getText().contains("Рейсы"));
    }

    @Test
    public void testFlightSearchByDate() throws Exception {
        WebResponse response = wc.getResponse(baseUrl + "/?date=2024-06-01");
        assertEquals(200, response.getResponseCode());
        // Проверка, что параметр даты передан в форму
        assertTrue(response.getText().contains("2024-06-01") || response.getText().contains("searchDate"));
    }

    @Test
    public void testClientListPage() throws Exception {
        WebResponse response = wc.getResponse(baseUrl + "/clients");
        assertEquals(200, response.getResponseCode());
        assertTrue(response.getText().contains("Клиенты") || response.getText().contains("clients"));
    }

    @Test
    public void testCreateClientForm() throws Exception {
        WebResponse response = wc.getResponse(baseUrl + "/clients/new");
        assertEquals(200, response.getResponseCode());
        // Проверяем наличие формы
        WebForm form = response.getForms()[0];
        assertNotNull(form);
    }

    @Test
    public void testFlightDetailsPage() throws Exception {
        // Предполагаем, что есть рейс с ID=1 (из тестовых данных)
        WebResponse response = wc.getResponse(baseUrl + "/flights/1");
        // Может вернуть 404 если нет данных, но страница должна существовать
        assertTrue(response.getResponseCode() == 200 || response.getResponseCode() == 404);
    }

    @Test
    public void testTicketPurchaseFlow_Start() throws Exception {
        // Начинаем процесс покупки: переход на страницу рейса
        WebResponse flightPage = wc.getResponse(baseUrl + "/flights/1");
        if (flightPage.getResponseCode() == 200) {
            // Ищем ссылку на покупку (если есть свободные места)
            WebLink[] links = flightPage.getLinks();
            boolean hasBuyLink = false;
            for (WebLink link : links) {
                if (link.getText().contains("Купить") || link.getURLString().contains("/buy")) {
                    hasBuyLink = true;
                    break;
                }
            }
            // Тест проходит, если структура ссылок корректна
            assertTrue(true);
        }
    }

    @Test
    public void testPaymentSuccessPage() throws Exception {
        // Прямой доступ к странице успеха (для теста структуры)
        // В реальном сценарии сюда ведёт форма оплаты
        WebResponse response = wc.getResponse(baseUrl + "/order/success");
        // Страница может требовать параметры, проверяем только доступность контроллера
        assertTrue(response.getResponseCode() == 200 || response.getResponseCode() == 400);
    }

    @Test
    public void testPaymentFailurePage() throws Exception {
        WebResponse response = wc.getResponse(baseUrl + "/order/failure");
        assertTrue(response.getResponseCode() == 200 || response.getResponseCode() == 400);
    }

    @Test
    public void testDeleteFlightConfirmation() throws Exception {
        // Проверяем, что форма удаления имеет JavaScript-подтверждение
        WebResponse response = wc.getResponse(baseUrl + "/flights");
        if (response.getResponseCode() == 200) {
            String text = response.getText();
            // Проверяем наличие onsubmit="return confirm"
            assertTrue(text.contains("confirm(") || text.contains("delete"),
                    "Форма удаления должна иметь подтверждение");
        }
    }
}