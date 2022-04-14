package org.gs;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@QuarkusTest
@TestHTTPEndpoint(AmazonCartResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AmazonCartResourceTest {

    @Test
    @Order(1)
    void getItemsEmptyList() {
        when()
                .get()
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", CoreMatchers.is(0));

    }

    @Test
    @Order(2)
    @TestSecurity(authorizationEnabled = false)
    void addItemAuthorizationDisabled() {
        JsonObject jsonItem = Json
                .createObjectBuilder()
                .add("id", "1")
                .add("name", "itemName")
                .add("quantity", "10")
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonItem.toString())
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", CoreMatchers.equalTo(1))
                .body("[0].id", CoreMatchers.equalTo(1))
                .body("[0].name", CoreMatchers.equalTo("itemName"))
                .body("[0].quantity", CoreMatchers.equalTo(10));
    }

    @Test
    @Order(3)
    @TestSecurity(user = "testUser", roles = {"writer"})
    void addItemWithAuthorization() {
        JsonObject jsonItem = Json.createObjectBuilder().add("id", "2").add("name", "itemName2").add("quantity", "12").build();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonItem.toString())
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", CoreMatchers.equalTo(2))
                .body("[1].id", CoreMatchers.equalTo(2))
                .body("[1].name", CoreMatchers.equalTo("itemName2"))
                .body("[1].quantity", CoreMatchers.equalTo(12));
    }

    @Test
    @Order(4)
    @TestSecurity(user = "testUser", roles = {"viewer"})
    void addItemWithWrongAuthorization() {
        JsonObject jsonItem = Json
                .createObjectBuilder()
                .add("id", "3")
                .add("name", "itemName3")
                .add("quantity", "10")
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonItem.toString())
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @Order(5)
    void addItemNoAuthorization() {
        JsonObject jsonItem = Json
                .createObjectBuilder()
                .add("id", "3")
                .add("name", "itemName3")
                .add("quantity", "10")
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonItem.toString())
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    @Order(6)
    @TestSecurity(authorizationEnabled = false)
    void deleteItemAuthorizationDisabled() {
        given()
                .when().delete("/1")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(7)
    @TestSecurity(user = "testUser", roles = {"admin", "writer"})
    void deleteItemWithAuthorization() {
        given()
                .when().delete("/2")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @Order(8)
    void deleteItemNoAuthorization() {
        given()
                .when().delete("/2")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}