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

import org.json.JSONException;
import org.json.JSONObject;

import com.tfg.app.AppApplication;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserAPITest {

        private String jwtToken;

        @BeforeEach
        public void setupTest() {
                RestAssured.useRelaxedHTTPSValidation();
                RestAssured.baseURI = "https://localhost:" + this.port + "/api";

        }

        @LocalServerPort
        int port;

        @Test
        @Order(1)
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
        @Order(3)
        public void registerApiTest() throws JSONException {
                loginApiTest();

                JSONObject body = new JSONObject();
                body.put("name", "Sergio");
                body.put("lastName", "Cuadros");
                body.put("username", "54366066W");
                body.put("email", "sercuaflores@gmail.com");
                body.put("passwordEncoded", "pass");
                body.put("address", "Calle...");
                body.put("city", "Madrid");
                body.put("country", "Spain");
                body.put("postalCode", "232");
                body.put("phone", "2322232");
                body.put("gender", "Masculino");
                body.put("birth", "2002-12-23");

                given()
                                .request()
                                .body(body.toString())
                                .contentType(ContentType.JSON)
                                .cookie("AuthToken", jwtToken)
                                .post("/users/")
                                .then()
                                .assertThat()
                                .statusCode(201);
        }

        @Test
        @Order(2)
        public void getUsersTest() {
                loginApiTest();

                String expectedJson = "[{\"id\":1,\"name\":\"Administrador\",\"lastName\":\"Administrador\",\"roles\":[\"ADMIN\"]},{\"id\":2,\"name\":\"Fernando\",\"lastName\":\"Ane\",\"username\":\"33W\",\"email\":\"admin100@smilelink.es\",\"phone\":\"711548969\",\"gender\":\"Masculino\",\"birth\":\"09-05-2024\",\"roles\":[\"DOCTOR\",\"ADMIN\"],\"codEntity\":100},{\"id\":4,\"name\":\"Jose Luis\",\"lastName\":\"Rodriguez\",\"username\":\"332W\",\"email\":\"admin200@smilelink.es\",\"phone\":\"785264122\",\"gender\":\"Masculino\",\"birth\":\"09-05-2024\",\"roles\":[\"DOCTOR\",\"ADMIN\"],\"codEntity\":200},{\"id\":5,\"name\":\"Jaime\",\"lastName\":\"Flores\",\"username\":\"37W\",\"email\":\"jaime.flores@smilelink.es\",\"phone\":\"444444444\",\"gender\":\"Masculino\",\"birth\":\"09-05-1993\",\"roles\":[\"DOCTOR\"],\"codEntity\":200},{\"id\":6,\"name\":\"Sergio\",\"lastName\":\"Cuadros Flores\",\"username\":\"54362066W\",\"email\":\"sercua.flores@gmail.com\",\"address\":\"Calle Benito Perez\",\"city\":\"Madrid\",\"country\":\"España\",\"postalCode\":\"28220\",\"phone\":\"444444444\",\"gender\":\"Masculino\",\"birth\":\"24-12-2002\",\"roles\":[\"USER\"],\"doctorAsignated\":{\"id\":4,\"name\":\"Jose Luis\",\"lastName\":\"Rodriguez\",\"username\":\"332W\",\"email\":\"admin200@smilelink.es\",\"phone\":\"785264122\",\"gender\":\"Masculino\",\"birth\":\"09-05-2024\",\"roles\":[\"DOCTOR\",\"ADMIN\"],\"codEntity\":200},\"codEntity\":200}]";

                Response response = given()
                                .cookie("AuthToken", jwtToken)
                                .contentType(ContentType.JSON)
                                .when()
                                .get("/users/userList")
                                .then()
                                .statusCode(200)
                                .extract()
                                .response();

                try {
                        JSONAssert.assertEquals(expectedJson, response.getBody().asString(), JSONCompareMode.LENIENT);
                } catch (JSONException e) {
                        e.printStackTrace();
                }
        }

        @Test
        @Order(4)
        public void getUsersNonAuthTest() {
                given()
                                .cookie("AuthToken", jwtToken)
                                .contentType(ContentType.JSON)
                                .when()
                                .get("/users/userList")
                                .then()
                                .assertThat()
                                .statusCode(403);
        }

        @Test
        public void getMeTest() throws JSONException {
                loginApiTest();

                JSONObject expectedJson = new JSONObject();
                expectedJson.put("id", 5);
                expectedJson.put("name", "Jaime");
                expectedJson.put("lastName", "Flores");
                expectedJson.put("username", "37W");
                expectedJson.put("email", "jaime.flores@smilelink.es");
                expectedJson.put("address", null);
                expectedJson.put("city", null);
                expectedJson.put("country", null);
                expectedJson.put("postalCode", null);
                expectedJson.put("phone", "444444444");
                expectedJson.put("gender", "Masculino");
                expectedJson.put("speciality", null);
                expectedJson.put("birth", "09-05-1993");
                expectedJson.put("doctorAsignated", null);
                expectedJson.put("codEntity", 200);

                Response response = given()
                                .cookie("AuthToken", jwtToken)
                                .contentType(ContentType.JSON)
                                .when()
                                .get("/users/me")
                                .then()
                                .statusCode(200)
                                .extract()
                                .response();

                JSONObject responseBody = new JSONObject(response.getBody().asString());

                JSONAssert.assertEquals(expectedJson.toString(), responseBody.toString(), JSONCompareMode.LENIENT);

        }

        @Test
        @Order(5)
        public void getUserByIdTest() throws JSONException {
                loginApiTest();

                JSONObject expectedJson = new JSONObject();
                expectedJson.put("id", 6);
                expectedJson.put("name", "Sergio");
                expectedJson.put("lastName", "Cuadros Flores");
                expectedJson.put("username", "54362066W");
                expectedJson.put("email", "sercua.flores@gmail.com");
                expectedJson.put("address", "Calle Benito Perez");
                expectedJson.put("city", "Madrid");
                expectedJson.put("country", "España");
                expectedJson.put("postalCode", "28220");
                expectedJson.put("phone", "444444444");
                expectedJson.put("gender", "Masculino");
                expectedJson.put("speciality", null);
                expectedJson.put("birth", "24-12-2002");

                Response response = given()
                                .cookie("AuthToken", jwtToken)
                                .contentType(ContentType.JSON)
                                .when()
                                .get("/users/6")
                                .then()
                                .statusCode(200)
                                .extract()
                                .response();

                JSONObject responseBody = new JSONObject(response.getBody().asString());

                JSONAssert.assertEquals(expectedJson.toString(), responseBody.toString(), JSONCompareMode.LENIENT);
        }

        @Test
        @Order(7)
        public void deteleteUserByIdTest() throws JSONException {
                loginApiTest();

                Response response = given()
                                .cookie("AuthToken", jwtToken)
                                .contentType(ContentType.JSON)
                                .when()
                                .delete("/users/6")
                                .then()
                                .statusCode(200)
                                .extract()
                                .response();

                String responseBody = response.getBody().asString();
                String expectedMessage = "UserData deleted for user with id: 6";
                assertEquals(expectedMessage, responseBody);
        }

        @Test
        @Order(6)
        public void updateUserTest() {
                loginApiTest();

                String updatedAddress = "Calle Benito";
                String updatedCity = "Madrid";
                String updatedCountry = "España";
                String updatedPostalCode = "28220";
                String updatedPhone = "278932";

                given()
                                .cookie("AuthToken", jwtToken)
                                .contentType("multipart/form-data")
                                .multiPart("address", updatedAddress)
                                .multiPart("city", updatedCity)
                                .multiPart("country", updatedCountry)
                                .multiPart("postalCode", updatedPostalCode)
                                .multiPart("phone", updatedPhone)
                                .when()
                                .put("/users/1")
                                .then()
                                .assertThat()
                                .statusCode(200)
                                .extract().response();
        }
}
