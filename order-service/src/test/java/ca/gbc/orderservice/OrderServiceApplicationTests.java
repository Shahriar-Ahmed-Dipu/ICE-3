package ca.gbc.orderservice;

import io.restassured.RestAssured;
import ca.gbc.orderservice.stub.InventoryClientStub;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");
//            .withDatabaseName("order-service")
//            .withUsername("admin")
//            .withPassword("password");

    @LocalServerPort
    private Integer port;

    static {
        postgreSQLContainer.start();
//        System.out.println("PostgreSQL container started: " + postgreSQLContainer.isRunning());
    }

    @BeforeEach
    void setup(){
        RestAssured.baseURI = "http://localhost";
//        if(port != null){
//            RestAssured.port = port;
//            System.out.println("Using port: " + port);
//        } else {
//            throw new IllegalStateException("Port is not initialized!");
//        }
        RestAssured.port = port;
    }

    @Test void shouldSubmitOrder(){
        String submitOrderJson = """
                {
                    "skuCode": "samsung_tv_2024",
                    "price": 5000,
                    "quantity": 10
                }
                """;


        //week 10
        //Mock a call to inventory-service
        InventoryClientStub.stubInventoryCall("samsung_tv_2024",10);

        RestAssured.given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("api/order")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .body("orderNumber", Matchers.notNullValue())
                .body("skuCode", Matchers.equalTo("samsung_tv_2024"))
                .body("price", Matchers.equalTo(5000))
                .body("quantity", Matchers.equalTo(10));

    }

    @Test
    void getAllOrdersTest(){
        String submitOrderJson = """
                {
                    "orderNumber": "test1",
                    "skuCode": "samsung_tv_2024",
                    "price": 5000,
                    "quantity": 10
                }
                """;


        //week 10
        //Mock a call to inventory-service
        InventoryClientStub.stubInventoryCall("samsung_tv_2024",10);

        RestAssured.given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("api/order")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .body().asString();

        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("api/order")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", Matchers.greaterThan(0))
                .body("[0].skuCode", Matchers.equalTo("samsung_tv_2024"))
                .body("[0].price", Matchers.equalTo(5000.0F))
                .body("[0].quantity",Matchers.equalTo(10));

    }

    @Test
    void updateOrderTest() {
        String submitOrderJson = """
            {
                "orderNumber": "test1",
                "skuCode": "samsung_tv_2024",
                "price": 5000,
                "quantity": 10
            }
            """;


        InventoryClientStub.stubInventoryCall("samsung_tv_2024", 10);

        var responseBodyString = RestAssured.given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("api/order")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath();

        var orderId = responseBodyString.get("id");


        String updateOrderJson = """
            {
                "skuCode": "samsung_tv_2025",
                "price": 6000,
                "quantity": 20
            }
            """;

        RestAssured.given()
                .contentType("application/json")
                .body(updateOrderJson)
                .when()
                .put("api/order/" + orderId)
                .then()
                .log().all()
                .statusCode(204);

        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("api/order/" + orderId)
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", Matchers.greaterThan(0))
                .body("skuCode", Matchers.equalTo("samsung_tv_2025"))
                .body("price", Matchers.equalTo(6000.0F))
                .body("quantity",Matchers.equalTo(20));

    }

    @Test
    void deleteOrderTest() {
        String submitOrderJson = """
            {
                "orderNumber": "test1",
                "skuCode": "samsung_tv_2024",
                "price": 5000,
                "quantity": 10
            }
            """;


        InventoryClientStub.stubInventoryCall("samsung_tv_2024", 10);

        RestAssured.given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("api/order")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .body()
                .asString();


        RestAssured.given()
                .contentType("application/json")
                .when()
                .delete("api/order/1")
                .then()
                .log().all()
                .statusCode(204);

    }




}
