package com.tfg.app.Test_Integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.tfg.app.AppApplication;

import static io.restassured.RestAssured.given;

@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserAPITest {

        // @Autowired
        // protected ObjectMapper objectMapper;

        @BeforeEach
        public void setupTest() {
                RestAssured.useRelaxedHTTPSValidation();
                RestAssured.baseURI = "https://localhost:" + this.port + "/api";
        }

        @LocalServerPort
        int port;

        @Test
        public void loginApiTest() {

                // ObjectNode user = objectMapper.createObjectNode()
                // .put("name", "NewUser_"+ type)
                // .put("email", type+"@urjc.es")
                // .put("password", "pass");

                // given()
                // .request()
                // .body(user)
                // .contentType(ContentType.JSON)

                // Datos del login
                String loginPayload = "{\n" +
                                "  \"username\": \"admin@smilelink.es\",\n" +
                                "  \"password\": \"superpassword12345\"\n" +
                                "}";

                // Realiza la solicitud POST y verifica la respuesta
                given()
                                .contentType(ContentType.JSON)
                                .body(loginPayload)
                                .when()
                                .post("/auth/login")
                                .then()
                                .assertThat()
                                .statusCode(200);

        }
}
