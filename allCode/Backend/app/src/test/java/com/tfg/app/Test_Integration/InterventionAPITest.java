package com.tfg.app.Test_Integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import com.tfg.app.AppApplication;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class InterventionAPITest {

    private String jwtToken;

    @BeforeEach
    public void setupTest() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:" + this.port + "/api";

    }

    @LocalServerPort
    int port;

    public void loginApiTest() {

        String loginPayload = "{\n" +
                "  \"username\": \"jaime.flores@smilelink.es\",\n" +
                "  \"password\": \"pass\"\n" +
                "}";

        io.restassured.response.Response response = given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response();

        Cookie jwtCookie = response.getDetailedCookie("AuthToken");

        jwtToken = jwtCookie.getValue();

    }

    @Test
    @Order(2)
    public void createInterventionToPatientTest() throws JSONException {
        loginApiTest();

        String type = "Empaste corona";

        given()
                .cookie("AuthToken", jwtToken)
                .contentType("multipart/form-data")
                .multiPart("type", type)
                .when()
                .post("/interventions/8/user=6")
                .then()
                .assertThat()
                .statusCode(201);

    }

    @Test
    @Order(1)

    public void getAllAInterventionsTest() throws JSONException {
        loginApiTest();

        // Construye el JSON esperado
        JSONArray expectedJson = new JSONArray("""
                    [{
                        "id": 27,
                        "type": "Mantenimiento y Prevención"
                    }]
                """);

        Response response = given()
                .cookie("AuthToken", jwtToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/interventions/all")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();

        try {
            JSONAssert.assertEquals(expectedJson.toString(), response.getBody().asString(), JSONCompareMode.LENIENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    public void deleteIntervention() {
        loginApiTest();

        Response response = given()
                .cookie("AuthToken", jwtToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/interventions/delete/27")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        String expectedMessage = "Intervention eliminado con éxito.";
        assertEquals(expectedMessage, responseBody);
    }

    @Test
    @Order(3)
    public void updateInterventionTest() {
        loginApiTest();

        String type = "Fisura";

        given()
                .cookie("AuthToken", jwtToken)
                .contentType("multipart/form-data")
                .multiPart("type", type)
                .when()
                .put("/interventions/update/27/user=6")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response();
    }

}