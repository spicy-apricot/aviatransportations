package obuhov.airline.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:systemtest;MODE=PostgreSQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class FlightSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void homePageLoads() {
        ResponseEntity<String> response = get("/");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("AviaTransport"));
        assertTrue(response.getBody().contains("Поиск рейсов"));
    }

    @Test
    void flightListPageLoads() {
        ResponseEntity<String> response = get("/flights");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Рейсы"));
        assertTrue(response.getBody().contains("confirm(") || response.getBody().contains("Рейсы не найдены"));
    }

    @Test
    void flightSearchKeepsDateInForm() {
        ResponseEntity<String> response = get("/?date=2024-06-01");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("2024-06-01"));
    }

    @Test
    void clientListAndCreateFormLoad() {
        ResponseEntity<String> list = get("/clients");
        ResponseEntity<String> form = get("/clients/new");

        assertEquals(HttpStatus.OK, list.getStatusCode());
        assertEquals(HttpStatus.OK, form.getStatusCode());
        assertTrue(list.getBody().contains("Клиенты"));
        assertTrue(form.getBody().contains("<form"));
        assertTrue(form.getBody().contains("name=\"name\""));
        assertTrue(form.getBody().contains("name=\"phoneNumber\""));
    }

    @Test
    void missingFlightReturns404InsteadOfServerError() {
        ResponseEntity<String> response = get("/flights/1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void paymentResultPagesLoad() {
        ResponseEntity<String> success = get("/order/success");
        ResponseEntity<String> failure = get("/order/failure");

        assertEquals(HttpStatus.OK, success.getStatusCode());
        assertEquals(HttpStatus.OK, failure.getStatusCode());
        assertTrue(success.getBody().contains("Заказ успешно оформлен"));
        assertTrue(failure.getBody().contains("Ошибка оплаты"));
    }

    private ResponseEntity<String> get(String path) {
        return rest.getForEntity("http://localhost:" + port + path, String.class);
    }
}
