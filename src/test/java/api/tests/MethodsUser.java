package api.tests;

import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static api.tests.BeforeAndAfter.*;
import static api.tests.MethodsOrders.createRequestBody;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static sun.nio.cs.Surrogate.is;

public class MethodsUser {


    @Step("Удалить пользователя")
    public void deleteUserByToken(String token) {
        String cleanToken = token.replace("Bearer ", "");
        Response deleteResponse = given()
                .header("Authorization", "Bearer " + cleanToken)
                .when()
                .delete(DELETE_USER_URL);
        assertThat(String.valueOf(deleteResponse.getStatusCode()), is(202));
        assertThat(deleteResponse.jsonPath().getBoolean("success"), equalTo(true));
        String expectedMessage = "User successfully removed";
        assertThat(deleteResponse.jsonPath().getString("message"), equalTo(expectedMessage));
    }

    @Step("Создать уникального пользователя")
    public Response createUniqueUser (String email, String password, String name) {
        Map<String, String> body = new HashMap<>();
        Response respons = given()
                .contentType(JSON)
                .body(body)
                .when()
                .post(REGISTER_URL);
        return respons;
    }

    @Step("Создать пользователя без password")
    public Response createUniqueUserWithoutPassword(String email, String name) {
        Map<String, String> body = createRequestBody(email, "", name);
        return given()
                .contentType(JSON)
                .body(body)
                .when()
                .post(REGISTER_URL);
    }
    @Step("Создать пользователя без email")
    public Response createUniqueUserWithoutEmail(String password, String name) {
        Map<String, String> body = createRequestBody("", password, name);
        return given()
                .contentType(JSON)
                .body(body)
                .when()
                .post(REGISTER_URL);
    }
    @Step("Создать пользователя без name")
    public Response createUniqueUserWithoutName(String password, String email) {
        Map<String, String> body = createRequestBody(email, password, "");
        return given()
                .contentType(JSON)
                .body(body)
                .when()
                .post(REGISTER_URL);
    }

    public String generateUniqueEmail() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 6);
        return "user" + uniqueId + "@ya.ru";
    }

    public String generateUniquePassword() {
        return "pass" + UUID.randomUUID().toString().substring(0, 6);
    }

    public String generateUniqueName() {
        return "User " + UUID.randomUUID().toString().substring(0, 6);
    }

    @Step("Логин под существующим пользователем")
    public static Response loginWithUser (String email, String password) {
        Map<String, String> body = createRequestBody(email, password, "");
        Response response = given()
                .contentType(JSON)
                .body(body)
                .when()
                .post(LOGIN_URL);
        return response;
    }

    @Step("Обновление email пользователя без авторизации")
    public static Response updateUserEmailWithoutAuth(String newEmail, String password, String name) {
        Map<String, String> requestBody = createRequestBody(newEmail, password, name);
        return given()
                .contentType(JSON)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }

    @Step("Обновление name пользователя без авторизации")
    public static Response updateUserNameWithoutAuth(String email, String password, String newName) {
        Map<String, String> requestBody = createRequestBody(email, password, newName);
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }
    @Step("Обновление password пользователя без авторизации")
    public static Response updateUserPasswordWithoutAuth(String email, String newPassword, String name) {
        Map<String, String> requestBody = createRequestBody(email, newPassword, name);
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }
    @Step("Обновление всех данных пользователя без авторизации")
    public static Response updateUserAllWithoutAuth(String newEmail, String newPassword, String newName) {
        Map<String, String> requestBody = createRequestBody(newEmail, newPassword, newName);
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }

    @Step("Проверка обновления всех полей пользователя с авторизацией")
    public static Response updateUserAllFields(String accessToken, String newEmail, String newPassword, String newName) {
        Map<String, String> requestBody = createRequestBody(newEmail, newPassword, newName);
        return given()
                .contentType(JSON)
                .header("Authorization", accessToken)
                .body(requestBody)
                .when()
                .patch(DELETE_USER_URL);
    }

    @Step("Проверка обновления всех данных пользователя с авторизацией")
    public static void validateUpdateAllFieldsResponse(Response response, String newEmail, String newName) {
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(newName));
    }
    @Step("Проверка ошибки доступа без авторизации")
    public static void validateUnauthorizedResponse(Response response) {
        response
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
