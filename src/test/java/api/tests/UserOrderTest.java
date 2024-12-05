package api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;
import io.qameta.allure.Description;
@Epic("User  Management")
@Feature("User  Orders")
public class UserOrderTest extends BeforeAndAfter {
    private MethodsOrders methodsOrders;
    private MethodsUser  methodsUser ;
    private Utils utils;
    public UserOrderTest() {
        methodsOrders = new MethodsOrders();
        methodsUser  = new MethodsUser ();
        utils = new Utils();
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
        Response orderResponse = methodsOrders.createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        methodsOrders.verifyOrderCreation(orderResponse);

    }

    @Test
    @Description("Создание заказа с авторизацией но без ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredients() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = methodsUser.loginWithUser(email, password);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = methodsOrders.createOrderWitNoIngredients(accessToken);
        orderResponse.then().log().all();
        utils.verifyOrderCreationNoIngredients(orderResponse);

    }
    @Test
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithNoAuthorization() {
        Response orderResponse = methodsOrders.createOrderWithoutAuthorization();
        orderResponse.then().log().all();
        methodsOrders.verifyOrderCreationUnauthorized(orderResponse);
    }
    @Test
    @Description("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithNoAuthorizationAndIngredients() {
        Response orderResponseWithoutAuthorization = methodsOrders.createOrderWithoutAuthorizationAndIngredients();
        orderResponseWithoutAuthorization.then().log().all();
        utils.verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);
        Response orderResponseWithoutIngredients = methodsOrders.createOrderWithoutAuthorizationAndIngredients();
        utils.verifyOrderCreationNoAuthorizedAndNoIngredients(orderResponseWithoutIngredients);
    }
    @Test
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsHash() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = MethodsUser.loginWithUser(email, password);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        try {
            Response orderResponse = MethodsOrders.createOrderWithInvalidIngredientsHash(accessToken);
            orderResponse.then().log().all();
            MethodsOrders.verifyOrderCreationInvalidIngredientsHash(orderResponse);
            orderResponse.then().statusCode(400);
        } catch (Exception e) {

            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        } finally {
            methodsUser.deleteUserByToken(accessToken);
        }
    }
}

