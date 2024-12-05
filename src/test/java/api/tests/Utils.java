package api.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

import static api.tests.MethodsOrders.createRequestBody;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static api.tests.BeforeAndAfter.DELETE_USER_URL;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static sun.nio.cs.Surrogate.is;

public class Utils {


    @Step("Проверка создания пользователя")
    public void verifyUserCreationSuccess(Response response) {
        assertThat(String.valueOf(response.getStatusCode()), is(200));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(true));
    }

    @Step("Проверка ошибки при повторной регистрации пользователя")
    public void verifyDuplicateUserError(Response response) {
        assertThat(String.valueOf(response.getStatusCode()), is(403));
        assertThat(response.jsonPath().getString("message"), equalTo("User already exists"));
    }


    @Step("Проверка ответа на неудачное создание пользователя без password")
    public void verifyUserCreationFailurePassword(Response response) {
        assertThat(String.valueOf(response.getStatusCode()), is(403));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(false));
        assertThat(response.jsonPath().getString("message"), equalTo("Email, password and name are required fields"));
    }


    @Step("Проверка ответа на неудачное создание пользователя без email")
    public void verifyUserCreationFailureEmail(Response response, String expectedMessage) {
        assertThat(String.valueOf(response.getStatusCode()), is(403));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(false));
        assertThat(response.jsonPath().getString("message"), equalTo(expectedMessage));
    }


    @Step("Проверка ответа на неудачное создание пользователя без name")
    public void verifyUserCreationFailureName(Response response, String expectedMessage) {
        assertThat(String.valueOf(response.getStatusCode()), is(403));
        assertThat(response.jsonPath().getBoolean("success"),equalTo(false));
        assertThat(response.jsonPath().getString("message"), equalTo(expectedMessage));
    }


    @Step("Проверка ответа успешного логина пользователя")
    public String verifyLoginSuccess(Response response) {
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getBoolean("success"), equalTo(true));
        return response.jsonPath().getString("accessToken");
    }

    @Step("Проверка ответа выполнения логина с неверными email и паролем")
    public static void verifyLoginWithInvalidCredentials(Response loginResponse) {
        loginResponse
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Проверка обновления email пользователя с авторизацией")
    public static Response updateUserEmail(String accessToken, String newEmail, String password, String name) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", newEmail);
        requestBody.put("password", password);
        requestBody.put("name", name);
        return given()
                .contentType(JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }

    @Step("Логирование данных запроса обновления email пользователя с авторизацией")
    public static void logRequest(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление email:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных ответа обновления email пользователя с авторизацией")
    public static void logResponse(Response response) {
        System.out.println("Ответ после обновления email:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка что статус код 200 и данные email пользователя с авторизацией обновлены")
    public static void validateUpdateResponse(Response response, String newEmail, String name) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(name));
    }

    @Step("Проверка обновления name пользователя с авторизацией")
    public static Response updateUserName(String accessToken, String email, String password, String newName) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("name", newName);
        return given()
                .contentType(JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }

    @Step("Логирование данных запроса обновления name пользователя с авторизацией")
    public static void logRequestName(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление name:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных ответа обновления name пользователя с авторизацией")
    public static void logResponseName(Response response) {
        System.out.println("Ответ после обновления name:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка, что статус код 200 и name пользователя с авторизацией обновлено")
    public static void validateUpdateNameResponse(Response response, String newName, String email) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(newName))
                .body("user.email", equalTo(email));
    }

    @Step("Логирование данных запроса на обновление email без авторизации")
    public static void logRequestWithoutAuth(String requestBody) {
        System.out.println("Запрос на обновление email без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных запроса на обновление name без авторизации")
    public static void logRequestWithoutAuthName(String requestBody) {
        System.out.println("Запрос на обновление name без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных запроса на обновление password без авторизации")
    public static void logRequestWithoutAuthPassword(String requestBody) {
        System.out.println("Запрос на обновление password без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Проверка обновления password пользователя с авторизацией")
    public static Response updateUserPassword(String accessToken, String newPassword) {
        Map<String, String> requestBody = createRequestBody("", newPassword, "");
        return given()
                .contentType(JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }

    @Step("Логирование данных запроса на обновление всех данных пользователя без авторизации")
    public static void logRequestWithoutAuthAll(String requestBody) {
        System.out.println("Запрос на обновление всех данных без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Метод для проверки кода и тела ответа ошибки создания заказа без ингредиентов но с авторизацией")
    public static void verifyOrderCreationNoIngredients(Response orderResponse) {
        orderResponse.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии авторизации")
    public static void verifyOrderCreationNoIngredientsUnauthorized(Response orderResponse) {
        orderResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии ингредиентов")
    public static void verifyOrderCreationNoAuthorizedAndNoIngredients(Response orderResponse) {
        orderResponse.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Логирование данных запроса обновления password пользователя с авторизацией")
    public static void logRequestPassword(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление password:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных ответа обновления password пользователя с авторизацией")
    public static void logResponsePassword(Response response) {
        System.out.println("Ответ после обновления password:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка кода и ответа обновления password пользователя с авторизацией")
    public static void validateUpdatePasswordResponse(Response response, String newPassword) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Логирование данных запроса на обновление всех полей пользователя с авторизацией")
    public static void logRequestAllFields(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление всех полей:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных ответа обновления всех данных пользователя с авторизацией")
    public static void logResponseAll(Response response) {
        System.out.println("Ответ после обновления всех полей:");
        System.out.println(response.prettyPrint());
    }


    public static class UserActions {
        @Step("Проверить ответ на создание пользователя")
        public String verifyUserCreation(Response response, String email, String name) {
            assertThat(String.valueOf(response.getStatusCode()), is(200));
            assertThat(response.jsonPath().getBoolean("success"), equalTo(true));
            assertThat(response.jsonPath().getString("user.email"), equalTo(email));
            assertThat(response.jsonPath().getString("user.name"),equalTo(name));
            String accessToken = "Bearer " + response.jsonPath().getString("accessToken");
            assertThat(accessToken, not(isEmptyOrNullString()));
            assertThat(response.jsonPath().getString("refreshToken"), not(isEmptyOrNullString()));
            return accessToken;
        }
    }
}
