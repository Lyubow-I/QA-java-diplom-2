package api.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static api.tests.BeforeAndAfter.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodsOrders {
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
    protected static Response createOrderWithInvalidIngredientsHash(String accessToken) {
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
        assertThat("Список заказов должен быть не пустым", orders.isEmpty());
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

    @Step("Метод для тела запроса")
    static Map<String, String> createRequestBody(String email, String password, String name) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("name", name);
        return requestBody;
    }
    @Step("Метод для тела запроса без name")
    static Map<String, String> createRequestBodyWithoutAName(String email, String password) {
        Map<String, String> requestBody = createRequestBody(email, password, "");
        return requestBody;
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
}

