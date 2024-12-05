package api.tests;


import io.restassured.response.Response;
import io.qameta.allure.Description;
import org.junit.Test;

import static api.tests.MethodsOrders.*;
import static api.tests.Utils.*;

public class OrderCreationTest extends BeforeAndAfter {
    private MethodsUser  methodsUser ;
    public OrderCreationTest() {
        methodsUser  = new MethodsUser ();

    }

    @Test
    @Description("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthorization() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = methodsUser.loginWithUser(email, password);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        verifyOrderCreation(orderResponse);
    }
    @Test
    @Description("Создание заказа с авторизацией и без ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredients() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = methodsUser.loginWithUser(email, password);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        verifyOrderCreationNoIngredients(orderResponse);
    }
    @Test
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithNoAuthorization() {
        Response orderResponse = createOrderWithoutAuthorization();
        orderResponse.then().log().all();
        verifyOrderCreationUnauthorized(orderResponse);
    }
    @Test
    @Description("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithNoAuthorizationAndIngredients() {
        Response orderResponseWithoutAuthorization = createOrderWithoutAuthorizationAndIngredients();
        orderResponseWithoutAuthorization.then().log().all();
        verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);
        Response orderResponseWithoutIngredients = createOrderWithoutAuthorizationAndIngredients();
        verifyOrderCreationNoAuthorizedAndNoIngredients(orderResponseWithoutIngredients);
    }
    @Test
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsHash() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = methodsUser.loginWithUser(email, password);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        try {
            Response orderResponse = MethodsOrders.createOrderWithInvalidIngredientsHash(accessToken);
            orderResponse.then().log().all();
            verifyOrderCreationInvalidIngredientsHash(orderResponse);
            orderResponse.then().statusCode(400);
        } catch (Exception e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        } finally {
        }
    }
}