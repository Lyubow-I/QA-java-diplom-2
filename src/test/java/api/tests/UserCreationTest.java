package api.tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
@Epic("User  Management")
@Feature("User  Creation and Deletion")
public class UserCreationTest {

    private Gson gson;
    private String password = "123456789";
    private String name = "Mars";
    private String token;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @After
    public void tearDown() {
        if (token != null) {
            deleteUser (token);
        }
    }

    private String formatResponseBody(String responseBody) {
        JsonElement jsonElement = JsonParser.parseString(responseBody);
        return gson.toJson(jsonElement);
    }

    private void checkStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode(), is(expectedStatusCode));
    }

    private void checkErrorMessage(Response response, String expectedMessage) {
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    private String createRequestBody(String email, String password, String name) {
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        data.put("name", name);
        return gson.toJson(data);
    }

    private void deleteUser (String token) {
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/auth/user");
        checkStatusCode(response, 200);
        System.out.println("Пользователь успешно удален.");
    }

    private String loginUser (String email, String password) {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createRequestBody(email, password, name))
                .when()
                .post("/api/auth/login");
        if (response.getStatusCode() == 200) {
            return response.jsonPath().getString("token");
        } else {
            return null;
        }
    }

    private String generateUniqueEmail() {
        return "testuser_" + System.currentTimeMillis() + "@gmail.com";
    }

    @Test
    @Description("Тест на успешное создание нового пользователя")
    public void testCreateUniqueUser () {
        String uniqueEmail = generateUniqueEmail();
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createRequestBody(uniqueEmail, password, name))
                .when()
                .post("/api/auth/register");
        checkStatusCode(response, 200);
        assertThat(response.jsonPath().get("success"), is(true));
        token = loginUser (uniqueEmail, password);
    }

    @Test
    @Description("Тест на попытку создать пользователя с уже существующим email")
    public void testCreateExistingUser () {
        String existingEmail = generateUniqueEmail();
        Response firstResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createRequestBody(existingEmail, password, name))
                .when()
                .post("/api/auth/register");
        checkStatusCode(firstResponse, 200);

        Response secondResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(createRequestBody(existingEmail, password, name))
                .when()
                .post("/api/auth/register");
        checkStatusCode(secondResponse, 403);
        checkErrorMessage(secondResponse, "User already exists");
    }

    @Test
    @Description("Тест на попытку создания пользователя без обязательного поля name")
    public void withoutRequiredFields() {
        String uniqueEmail = generateUniqueEmail();
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\": \"" + uniqueEmail + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/auth/register");

        checkStatusCode(response, 403);
        checkErrorMessage(response, "Email, password and name are required fields");
    }
    @Test
    @Description("Тест на попытку создания пользователя без поля email")
    public void withoutEmail() {
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"password\": \"" + password + "\", \"name\": \"" + name + "\"}")
                .when()
                .post("/api/auth/register");

        checkStatusCode(response, 403);
        checkErrorMessage(response, "Email, password and name are required fields");
    }

    @Test
    @Description("Тест на попытку создания пользователя без поля password")
    public void withoutPassword() {
        String uniqueEmail = generateUniqueEmail();
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"email\": \"" + uniqueEmail + "\", \"name\": \"" + name + "\"}") // Пропускаем пароль
                .when()
                .post("/api/auth/register");

        checkStatusCode(response, 403);
        checkErrorMessage(response, "Email, password and name are required fields");
    }
}


