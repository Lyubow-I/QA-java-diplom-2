package api.tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("User  Management")
@Feature("User  Orders")
public class UserOrderTest {
    private Gson gson;
    private String email;
    private String password;
    private String accessToken;
    private boolean testPassed;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        gson = new GsonBuilder().setPrettyPrinting().create();
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
        assertThat(loginResponse.jsonPath().getBoolean("success"), is(true));
        return loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Step
    public void getOrdersAsAuthorizedUser () {
        testPassed = true;
        Response response = RestAssured.given()
                .header("Authorization", accessToken)
                .when()
                .get("/api/orders");

        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Step
    public void getOrdersAsUnauthorizedUser () {
        testPassed = true;
        Response response = RestAssured.given()
                .when()
                .get("/api/orders");

        assertThat(response.getStatusCode(), is(401));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is("You should be authorised"));
    }
}

