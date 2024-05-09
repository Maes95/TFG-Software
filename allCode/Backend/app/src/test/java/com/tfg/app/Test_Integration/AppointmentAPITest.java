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
import org.json.JSONObject;

import com.tfg.app.AppApplication;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AppointmentAPITest {

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
    public void createAppointmentTest() throws JSONException {
        loginApiTest();
        JSONObject requestBody = new JSONObject();
        requestBody.put("bookDate", "2021-02-21");
        requestBody.put("fromDate", "12:33:02");
        requestBody.put("toDate", "13:34:03");
        requestBody.put("description", "Cita para revisión general");

        given()
                .cookie("AuthToken", jwtToken)
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("/appointments/")
                .then()
                .assertThat()
                .statusCode(201);

    }

    @Test
    @Order(1)
    public void getAllAppointmentsTest() throws JSONException {
        loginApiTest();

        // Construye el JSON esperado
        JSONArray expectedJson = new JSONArray("""
                    [{
                        "id": 8,
                        "bookDate": "2024-07-25",
                        "fromDate": "11:45:00",
                        "toDate": "12:05:00",
                        "description": "Mantenimiento y Prevención",
                        "additionalNote": null
                    }]
                """);

        Response response = given()
                .cookie("AuthToken", jwtToken)
                .contentType(ContentType.JSON) // Especificar que el cuerpo es JSON
                .when()
                .get("/appointments/all")
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
    @Order(3)
    public void getAppointmentTest() throws JSONException {
        loginApiTest();

        // Construye el JSON esperado
        JSONObject expectedJson = new JSONObject("""
                    {
                        "id": 8,
                        "bookDate": "2024-07-25",
                        "fromDate": "11:45:00",
                        "toDate": "12:05:00",
                        "description": "Mantenimiento y Prevención",
                        "additionalNote": null
                    }
                """);

        Response response = given()
                .cookie("AuthToken", jwtToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/appointments/8")
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
    public void fullupdateAppointmentTest() {
        loginApiTest();

        String bookDate = "2021-02-21";
        String fromDate = "12:35:02";
        String toDate = "13:34:03";
        String description = "Cita para general";

        given()
                .cookie("AuthToken", jwtToken)
                .contentType("multipart/form-data")
                .multiPart("bookDate", bookDate)
                .multiPart("fromDate", fromDate)
                .multiPart("toDate", toDate)
                .multiPart("description", description)
                .when()
                .put("/appointments/fullupdate/8")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().response();
    }

    @Test
    @Order(5)
    public void deleteAppointment() {
        loginApiTest();

        Response response = given()
                .cookie("AuthToken", jwtToken)
                .contentType(ContentType.JSON)
                .when()
                .delete("/appointments/delete/8")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String responseBody = response.getBody().asString();
        String expectedMessage = "Appointment eliminado con éxito.";
        assertEquals(expectedMessage, responseBody);
    }
}