package api.tests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("User  Management")
@Feature("User  Login")
public class UserLoginTest {
    private Gson gson;
    private String email;
    private String password;
    private String accessToken;
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        gson = new GsonBuilder().setPrettyPrinting().create();
        email = "testuser_" + System.currentTimeMillis() + "@gmail.com";
        password = "password123";
        createUser (email, password);
        accessToken = getAccessToken(email, password);
    }

    @After
    public void tearDown() {
        deleteUser(accessToken); // Удаляем пользователя после теста
    }

    private void createUser (String email, String password) {
        String body = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"Test User\" }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/auth/register");
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true)); // Убедитесь, что регистрация успешна
        assertThat(response.jsonPath().getString("accessToken"), is(notNullValue())); // Убедитесь, что токен не пустой
        System.out.println("Пользователь успешно создан: " + email);
    }
    private String getAccessToken(String email, String password) {
        String body = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/auth/login");
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        String token = response.jsonPath().getString("accessToken");
        assertThat(token, is(notNullValue()));
        System.out.println("Полученный accessToken: " + token);
        return token; // Возвращаем токен
    }

    private void deleteUser(String accessToken) {

        accessToken = getAccessToken(email, password);

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/auth/user");

        if (response.getStatusCode() == 200) {
            System.out.println("Пользователь успешно удален.");
        } else {
            System.out.println("Ошибка при удалении пользователя. Статус код: " + response.getStatusCode());
            System.out.println("Ответ от сервера: " + response.asString());
        }
    }
    @Test
    @DisplayName("Авторизация")
    @Step("Пользователь может авторизоваться с корректными данными")
    public void loginWithValidCredentials() {
        String loginBody = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("/api/auth/login");

        assertThat(loginResponse.getStatusCode(), is(200));
        assertThat(loginResponse.jsonPath().getBoolean("success"), is(true));
        assertThat(loginResponse.jsonPath().getString("accessToken"), is(notNullValue()));
        assertThat(loginResponse.jsonPath().getString("user.email"), is(email));
        assertThat(loginResponse.jsonPath().getString("user.name"), is("Test User"));
        accessToken = loginResponse.jsonPath().getString("accessToken");
        System.out.println("Пользователь авторизован: " + email);
    }



    @Test
    @DisplayName("Проверка авторизации с неверными данными")
    @Step("Пользователь не может авторизоваться с неверными данными")
    public void loginWithInvalidCredentials() {
        String invalidLoginBody = "{ \"email\": \"wrongemail@example.com\", \"password\": \"wrongpassword\" }";
        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(invalidLoginBody)
                .when()
                .post("/api/auth/login");

        assertThat(loginResponse.getStatusCode(), is(401));
        assertThat(loginResponse.jsonPath().getBoolean("success"), is(false));
        assertThat(loginResponse.jsonPath().getString("message"), is("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка авторизации без email")
    @Step("Пользователь не может авторизоваться без email")
    public void loginWithoutEmail() {
        String loginBody = "{ \"password\": \"" + password + "\" }";
        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("/api/auth/login");

        assertThat(loginResponse.getStatusCode(), is(401));
        assertThat(loginResponse.jsonPath().getBoolean("success"), is(false));
        assertThat(loginResponse.jsonPath().getString("message"), is("email or password are incorrect"));
    }


    @Test
    @DisplayName("Проверка авторизации без password")
    @Step("Пользователь не может авторизоваться без пароля")
    public void loginWithoutPassword() {
        String loginBody = "{ \"email\": \"" + email + "\" }";
        Response loginResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("/api/auth/login");

        assertThat(loginResponse.getStatusCode(), is(401));
        assertThat(loginResponse.jsonPath().getBoolean("success"), is(false));
        assertThat(loginResponse.jsonPath().getString("message"), is("email or password are incorrect"));
    }
}
