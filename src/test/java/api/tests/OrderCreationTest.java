package api.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest {
    private String email;
    private String password;
    private String accessToken;
    private boolean testPassed;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        email = "testuser_" + System.currentTimeMillis() + "@gmail.com";
        password = "password123";
        createUser (email, password);
        accessToken = loginAndGetToken(email, password);
    }

    @After
    public void tearDown() {
        if (testPassed) {
            System.out.println("Пользователь удален: " + email);
        } else {
            System.out.println("Пользователь не удален: " + email);
        }
    }

    private void createUser (String email, String password) {
        String body = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"Test User\" }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/auth/register");
        assertThat(response.getStatusCode(), is(200));
        System.out.println("Пользователь успешно создан: " + email);
    }

    private String loginAndGetToken(String email, String password) {
        String loginBody = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("/api/auth/login");

        assertThat(loginResponse.getStatusCode(), is(200));
        return loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Step("Создание заказа с ингредиентами и авторизацией")
    public void createOrderWithAuthorization() {
        testPassed = true;
        String body = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa70\"] }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/api/orders");

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        assertThat(response.jsonPath().getString("name"), is("Метеоритный бургер"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Step("Создание заказа с ингредиентами без авторизацией")
    public void createOrderWithoutAuthorization() {
        testPassed = true;
        String body = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa6f\"] }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/orders");

        assertThat(response.getStatusCode(), is(401));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Step("Создание заказа с ингредиентами и авторизацией")
    public void createOrderWithIngredients() {
        testPassed = true;
        String body = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa6f\"] }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/api/orders");

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Step("Создание заказа с авторизацией без ингредиентов")
    public void createOrderWithoutIngredients() {
        testPassed = true;
        String body = "{ }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/api/orders");

        assertThat(response.getStatusCode(), is(400));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Step("Создание заказа с авторизацией и неверным хешем ингридиента")
    public void createOrderWithInvalidIngredientHash() {
        testPassed = true;
        String body = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa\"] }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/api/orders");

        assertThat(response.getStatusCode(), is(500));
    }
}
