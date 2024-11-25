package api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Управление пользователями")
@Feature("Обновление пользователя")
public class UserUpdateTest {
    @Rule
    public TestName testName = new TestName();
    private Gson gson;
    private String email;
    private String password;
    private String accessToken;
    private boolean testPassed;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        email = "testuser_" + System.currentTimeMillis() + "@gmail.com"; // Генерация уникального email
        password = "password123"; // Пароль для теста
        createUser(email, password); // Создание пользователя перед тестами
        accessToken = loginAndGetToken(email, password); // Получение токена
    }

    @After
    public void tearDown() {
        // Здесь можно добавить логику для удаления пользователя, если необходимо
        if (testPassed) {
            System.out.println("Пользователь удален: " + email);
        } else {
            System.out.println("Пользователь не удален: " + email);
        }
    }

    private void createUser(String email, String password) {
        String body = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"name\": \"Test User\" }";
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/auth/register");
        assertThat(response.getStatusCode(), is(200));
        System.out.println("Пользователь успешно создан: " + email);// Проверка успешного создания пользователя
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
    @DisplayName("Авторизованный пользователь может изменить поле имя")
    @Step
    public void authorizedUserCanChangeName() {
        testPassed = true;
        String newName = "New Name";
        Response response = updateUserData("name", newName);
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        assertThat(response.jsonPath().getString("user.name"), is(newName));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить поле логин")
    @Step
    public void authorizedUserCanChangeLogin() {
        testPassed = true;
        String newEmail = "newemail_" + System.currentTimeMillis() + "@gmail.com";
        Response response = updateUserData("email", newEmail);
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        assertThat(response.jsonPath().getString("user.email"), is(newEmail));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить поле пароль")
    @Step
    public void authorizedUserCanChangePassword() {
        testPassed = true;
        String newPassword = "newpassword123";
        Response response = updateUserData("password", newPassword);
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));

    }

    @Test
    @DisplayName("Не авторизованный пользователь не может изменить поле имя")
    @Step
    public void unauthorizedUserCannotChangeName() {
        testPassed = true;
        Response response = updateUserDataWithoutAuth("name", "New Name");
        assertThat(response.getStatusCode(), is(401));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is("You should be authorised"));
    }

    @Test
    @DisplayName("Не авторизованный пользователь не может изменить поле логин")
    @Step
    public void unauthorizedUserCannotChangeLogin() {
        testPassed = true;
        Response response = updateUserDataWithoutAuth("email", "newemail@example.com");
        assertThat(response.getStatusCode(), is(401));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is("You should be authorised"));
    }

    @Test
    @DisplayName("Не авторизованный пользователь не может изменить поле пароль")
    @Step
    public void unauthorizedUserCannotChangePassword() {
        testPassed = true;
        Response response = updateUserDataWithoutAuth("password", "newpassword123");
        assertThat(response.getStatusCode(), is(401));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is("You should be authorised"));
    }

    private Response updateUserData(String field, String value) {
        String body = String.format("{ \"%s\": \"%s\" }", field, value);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .patch("/api/auth/user");
    }

    private Response updateUserDataWithoutAuth(String field, String value) {
        String body = String.format("{ \"%s\": \"%s\" }", field, value);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .patch("/api/auth/user");
    }

    private String getCurrentTestName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}

