package api.tests;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

    public class AllMethods {
        protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
        protected static final String REGISTER_URL = BASE_URL + "/api/auth/register";
        protected static final String LOGIN_URL = BASE_URL + "/api/auth/login";
        protected static final String DELETE_USER_URL = BASE_URL + "/api/auth/user";
        protected static final String ORDERS_URL = BASE_URL + "/api/orders";
        protected static final String INGREDIENTS_URL = BASE_URL + "/api/ingredients";
        protected String accessToken;


        @Before
        public void setUp() {
            RestAssured.baseURI = BASE_URL;
        }

        @After
        public void tearDown() {
            if (accessToken != null && !accessToken.isEmpty()) {
                deleteUserByToken(accessToken);
            }
        }

        @Step("Удалить пользователя")
        public void deleteUserByToken(String token) {
            String cleanToken = token.replace("Bearer ", "");
            Response deleteResponse = given()
                    .header("Authorization", "Bearer " + cleanToken)
                    .when()
                    .delete(DELETE_USER_URL);
            assertThat(deleteResponse.getStatusCode(), is(202));
            assertThat(deleteResponse.jsonPath().getBoolean("success"), is(true));
            String expectedMessage = "User successfully removed";
            assertThat(deleteResponse.jsonPath().getString("message"), is(expectedMessage));
        }

        @Step("Создать уникального пользователя")
        public Response createUniqueUser (String email, String password, String name) {
            String body = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}", email, password, name);
            Response respons = given()
                    .contentType(JSON)
                    .body(body)
                    .when()
                    .post(REGISTER_URL);
            return respons;
        }

        @Step("Проверить ответ на создание пользователя")
        public String verifyUserCreation(Response response, String email, String name) {
            assertThat(response.getStatusCode(), is(200));
            assertThat(response.jsonPath().getBoolean("success"), is(true));
            assertThat(response.jsonPath().getString("user.email"), is(email));
            assertThat(response.jsonPath().getString("user.name"), is(name));
            String accessToken = "Bearer " + response.jsonPath().getString("accessToken");
            assertThat(accessToken, not(isEmptyOrNullString()));
            assertThat(response.jsonPath().getString("refreshToken"), not(isEmptyOrNullString()));
            return accessToken;
        }

        @Step("Проверка создания пользователя")
        public void verifyUserCreationSuccess(Response response) {
            assertThat(response.getStatusCode(), is(200));
            assertThat(response.jsonPath().getBoolean("success"), is(true));
        }

        @Step("Проверка ошибки при повторной регистрации пользователя")
        public void verifyDuplicateUserError(Response response) {
            assertThat(response.getStatusCode(), is(403));
            assertThat(response.jsonPath().getString("message"), equalTo("User already exists"));
        }

        @Step("Создать пользователя без password")
        public Response createUniqueUserWithoutPassword(String email, String name) {
            String body = String.format("{\"email\": \"%s\", \"name\": \"%s\"}", email, name);
            return given()
                    .contentType(JSON)
                    .body(body)
                    .when()
                    .post(REGISTER_URL);
        }

        @Step("Проверка ответа на неудачное создание пользователя без password")
        public void verifyUserCreationFailurePassword(Response response) {
            assertThat(response.getStatusCode(), is(403));
            assertThat(response.jsonPath().getBoolean("success"), is(false));
            assertThat(response.jsonPath().getString("message"), is("Email, password and name are required fields"));
        }

        @Step("Создать пользователя без email")
        public Response createUniqueUserWithoutEmail(String password, String name) {
            String body = String.format("{\"password\": \"%s\", \"name\": \"%s\"}", password, name);
            return given()
                    .contentType(JSON)
                    .body(body)
                    .when()
                    .post(REGISTER_URL);
        }

        @Step("Проверка ответа на неудачное создание пользователя без email")
        public void verifyUserCreationFailureEmail(Response response, String expectedMessage) {
            assertThat(response.getStatusCode(), is(403));
            assertThat(response.jsonPath().getBoolean("success"), is(false));
            assertThat(response.jsonPath().getString("message"), is(expectedMessage));
        }

        @Step("Создать пользователя без name")
        public Response createUniqueUserWithoutName(String password, String email) {
            String body = String.format("{\"password\": \"%s\", \"email\": \"%s\"}", password, email);
            return given()
                    .contentType(JSON)
                    .body(body)
                    .when()
                    .post(REGISTER_URL);
        }

        @Step("Проверка ответа на неудачное создание пользователя без name")
        public void verifyUserCreationFailureName(Response response, String expectedMessage) {
            assertThat(response.getStatusCode(), is(403));
            assertThat(response.jsonPath().getBoolean("success"), is(false));
            assertThat(response.jsonPath().getString("message"), is(expectedMessage));
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
        public Response loginWithUser (String email, String password, String name) {
            String body = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}", email, password, name);
            Response response = given()
                    .contentType(JSON)
                    .body(body)
                    .when()
                    .post(LOGIN_URL);
            return response;
        }

        @Step("Проверка ответа успешного логина пользователя")
        public String verifyLoginSuccess(Response response) {
            assertThat(response.getStatusCode(), equalTo(200));
            assertThat(response.jsonPath().getBoolean("success"), is(true));
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
            String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
            return given()
                    .contentType(JSON)
                    .header("Authorization", accessToken)
                    .body(requestBody)
                    .when()
                    .patch(DELETE_USER_URL);
        }
        @Step ("Логирование данных запроса обновления email пользователя с авторизацией")
        public static void logRequest(String accessToken, String requestBody) {
            System.out.println("Запрос на обновление email:");
            System.out.println("Токен авторизации: " + accessToken);
            System.out.println("Тело запроса: " + requestBody);
        }

        @Step ("Логирование данных ответа обновления email пользователя с авторизацией")
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
        @Step ("Проверка обновления name пользователя с авторизацией")
        public static Response updateUserName(String accessToken, String email, String password, String newName) {
            String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
            return given()
                    .contentType(JSON)
                    .header("Authorization", accessToken)
                    .body(requestBody)
                    .when()
                    .patch("/api/auth/user");
        }

        @Step ("Логирование данных запроса обновления name пользователя с авторизацией")
        public static void logRequestName(String accessToken, String requestBody) {
            System.out.println("Запрос на обновление name:");
            System.out.println("Токен авторизации: " + accessToken);
            System.out.println("Тело запроса: " + requestBody);
        }

        @Step ("Логирование данных ответа обновления name пользователя с авторизацией")
        public static void logResponseName(Response response) {
            System.out.println("Ответ после обновления name:");
            System.out.println(response.prettyPrint());
        }
        @Step("Обновление email пользователя без авторизации")
        public static Response updateUserEmailWithoutAuth(String newEmail, String password, String name) {
            String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
            return given()
                    .contentType(JSON)
                    .body(requestBody)
                    .when()
                    .patch(DELETE_USER_URL);
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


        @Step("Обновление name пользователя без авторизации")
        public static Response updateUserNameWithoutAuth(String email, String password, String newName) {
            String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
            return given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .patch("/api/auth/user");
        }

        @Step("Логирование данных запроса на обновление name без авторизации")
        public static void logRequestWithoutAuthName(String requestBody) {
            System.out.println("Запрос на обновление name без авторизации:");
            System.out.println("Тело запроса: " + requestBody);
        }

        @Step("Обновление password пользователя без авторизации")
        public static Response updateUserPasswordWithoutAuth(String email, String newPassword, String name) {
            String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" + newPassword + "\"}";
            return given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .patch("/api/auth/user");
        }

        @Step("Логирование данных запроса на обновление password без авторизации")
        public static void logRequestWithoutAuthPassword(String requestBody) {
            System.out.println("Запрос на обновление password без авторизации:");
            System.out.println("Тело запроса: " + requestBody);
        }
        @Step("Проверка обновления password пользователя с авторизацией")
        public static Response updateUserPassword(String accessToken, String newPassword) {
            String requestBody = "{\"password\":\"" + newPassword + "\"}";
            return given()
                    .contentType(JSON)
                    .header("Authorization", accessToken)
                    .body(requestBody)
                    .when()
                    .patch("/api/auth/user");
        }
        @Step("Обновление всех данных пользователя без авторизации")
        public static Response updateUserAllWithoutAuth(String newEmail, String newPassword, String newName) {
            String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
            return given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .patch("/api/auth/user");
        }
        @Step("Логирование данных запроса на обновление всех данных пользователя без авторизации")
        public static void logRequestWithoutAuthAll(String requestBody) {
            System.out.println("Запрос на обновление всех данных без авторизации:");
            System.out.println("Тело запроса: " + requestBody);
        }
        @Step("Метод для запроса создания заказа без авторизации и без ингредиентов")
        public static Response createOrderWithoutAuthorizationAndIngredients() {
            String orderRequestBody = "{\n" +
                    "  \"ingredients\": []\n" +
                    "}";
            return given()
                    .contentType(JSON)
                    .body(orderRequestBody)
                    .when()
                    .post(ORDERS_URL);
        }
        @Step("Метод для проверки ошибки запроса создания заказа с ингредиентами но без авторизации")
        public static Response createOrderWithoutAuthorization() {
            Response ingredientsResponse = given()
                    .when()
                    .get(INGREDIENTS_URL)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");
            String orderRequestBody = String.format("{\n" +
                    "  \"ingredients\": [\"%s\", \"%s\"]\n" +
                    "}", ingredientIds.get(0), ingredientIds.get(1));
            return given()
                    .contentType(JSON)
                    .body(orderRequestBody)
                    .when()
                    .post(ORDERS_URL);
        }

        @Step("Метод для проверки кода и тела ответа при отсутствии авторизации создания заказа с ингредиентами")
        public static void verifyOrderCreationUnauthorized(Response orderResponse) {
            orderResponse.then()
                    .statusCode(401)
                    .body("success", equalTo(false))
                    .body("message", equalTo("You should be authorised"));
            System.out.println("Метод для проверки ошибки кода и тела ответа при отсутствии авторизации создания заказа с ингредиентами отработал успешно");
        }
        @Step("Метод для запроса создания заказа с авторизацией но без ингредиентов")
        public static Response createOrderWitNoIngredients(String accessToken) {
            String orderRequestBody = "{\n" +
                    "  \"ingredients\": []\n" +
                    "}";
            return given()
                    .contentType(JSON)
                    .header("Authorization", accessToken)
                    .body(orderRequestBody)
                    .when()
                    .post(ORDERS_URL);
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
        @Step ("Логирование данных запроса обновления password пользователя с авторизацией")
        public static void logRequestPassword(String accessToken, String requestBody) {
            System.out.println("Запрос на обновление password:");
            System.out.println("Токен авторизации: " + accessToken);
            System.out.println("Тело запроса: " + requestBody);
        }

        @Step ("Логирование данных ответа обновления password пользователя с авторизацией")
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

        @Step("Проверка обновления всех полей пользователя с авторизацией")
        public static Response updateUserAllFields(String accessToken, String newEmail, String newPassword, String newName) {
            String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
            return given()
                    .contentType(JSON)
                    .header("Authorization", accessToken)
                    .body(requestBody)
                    .when()
                    .patch("/api/auth/user");
        }

        @Step("Логирование данных запроса на обновление всех полей пользователя с авторизацией")
        public static void logRequestAllFields(String accessToken, String requestBody) {
            System.out.println("Запрос на обновление всех полей:");
            System.out.println("Токен авторизации: " + accessToken);
            System.out.println("Тело запроса: " + requestBody);
        }

        @Step ("Логирование данных ответа обновления всех данных пользователя с авторизацией")
        public static void logResponseAll(Response response) {
            System.out.println("Ответ после обновления всех полей:");
            System.out.println(response.prettyPrint());
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

        @Step("Метод для запроса создания заказа с ингредиентами и авторизацией")
        public static Response createOrderWithIngredients(String accessToken) {
            Response ingredientsResponse = given()
                    .when()
                    .get(INGREDIENTS_URL)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            List<String> ingredientIds = ingredientsResponse.jsonPath().getList("data._id");
            String orderRequestBody = String.format("{\"ingredients\": [\"%s\", \"%s\"]}",
                    ingredientIds.get(0), ingredientIds.get(1));
            return given()
                    .contentType(JSON)
                    .header("Authorization", accessToken)
                    .body(orderRequestBody)
                    .when()
                    .post(ORDERS_URL);
        }
        @Step("Метод для запроса создания заказа с неверным хешем ингредиентов")
        public static Response createOrderWithInvalidIngredientsHash(String accessToken) {
            String orderRequestBody = "{\n" +
                    "  \"ingredients\": [\"invalidHash1\", \"invalidHash2\"]\n" +
                    "}";
            return given()
                    .contentType(JSON)
                    .header("Authorization", accessToken)
                    .body(orderRequestBody)
                    .when()
                    .post(ORDERS_URL);
        }

        @Step("Метод для проверки кода и тела ответа при неверном хеше ингредиентов")
        public static void verifyOrderCreationInvalidIngredientsHash(Response orderResponse) {
            orderResponse.then()
                    .statusCode(500)
                    .body("success", equalTo(false));
        }
        @Step("Метод для проверки кода и тела ответа успешности создания заказа с ингредиентами и атворизацией")
        public static void verifyOrderCreation(Response orderResponse) {
            orderResponse.then()
                    .statusCode(200)
                    .body("success", equalTo(true));
        }

        @Step("Метод для получения списка заказов авторизованного пользователя")
        public static Response getUserOrders(String accessToken) {
            Response response = given()
                    .header("Authorization", accessToken)
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .extract().response();
            return response;
        }

        @Step("Метод для проверки ответа успешного получения списка заказов")
        public static void verifyUserOrdersRetrieval(Response ordersResponse) {
            ordersResponse.then()
                    .statusCode(200)
                    .body("success", equalTo(true));
            List<Map<String, Object>> orders = ordersResponse.jsonPath().getList("orders");
            assertThat("Список заказов должен быть не пустым", orders, not(empty()));
        }

        @Step("Метод проверки ошибки для получения списка заказов без авторизации")
        public static Response getUserOrdersWithoutAuthorization() {
            Response response = given()
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .extract().response();
            return response;
        }

        @Step("Метод для проверки ответа ошибки получения списка заказов пользователя без авторизации")
        public static void verifyUnauthorizedResponse(Response response) {
            response.then()
                    .statusCode(401)
                    .body("success", equalTo(false))
                    .body("message", equalTo("You should be authorised"));
        }
    }

