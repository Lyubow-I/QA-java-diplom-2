package api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Test;

@Epic("User  Management")
@Feature("User  Orders")
public class UserOrderTest extends AllMethods {

    private AllMethods methodsUserLogin = new AllMethods();

    @Test
    @Description("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthorization() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = AllMethods.createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        AllMethods.verifyOrderCreation(orderResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Создание заказа с авторизацией но без ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredients() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = AllMethods.createOrderWitNoIngredients(accessToken);
        orderResponse.then().log().all();
        AllMethods.verifyOrderCreationNoIngredients(orderResponse);
        deleteUserByToken(accessToken);
    }
    @Test
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithNoAuthorization() {
        Response orderResponse = AllMethods.createOrderWithoutAuthorization();
        orderResponse.then().log().all();
        AllMethods.verifyOrderCreationUnauthorized(orderResponse);
    }
    @Test
    @Description("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithNoAuthorizationAndIngredients() {
        Response orderResponseWithoutAuthorization = AllMethods.createOrderWithoutAuthorizationAndIngredients();
        orderResponseWithoutAuthorization.then().log().all();
        AllMethods.verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);
        Response orderResponseWithoutIngredients = AllMethods.createOrderWithoutAuthorizationAndIngredients();
        AllMethods.verifyOrderCreationNoAuthorizedAndNoIngredients(orderResponseWithoutIngredients);
    }
    @Test
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsHash() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        try {
            Response orderResponse = AllMethods.createOrderWithInvalidIngredientsHash(accessToken);
            orderResponse.then().log().all();
            AllMethods.verifyOrderCreationInvalidIngredientsHash(orderResponse);
            orderResponse.then().statusCode(400);
        } catch (Exception e) {

            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        } finally {
            deleteUserByToken(accessToken);
        }
    }
}

