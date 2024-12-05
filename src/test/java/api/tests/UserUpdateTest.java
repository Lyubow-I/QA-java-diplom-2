package api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Test;

import java.util.Map;

import static api.tests.MethodsOrders.createRequestBody;
import static api.tests.MethodsUser.*;
import static api.tests.Utils.*;


@Epic("Управление пользователями")
@Feature("Обновление пользователя")
public class UserUpdateTest extends BeforeAndAfter {
    private MethodsOrders methodsOrders;
    private MethodsUser  methodsUser ;
    private Utils.UserActions userActions;
    private Utils utils;
    public UserUpdateTest() {
        methodsOrders = new MethodsOrders();
        methodsUser  = new MethodsUser ();
        userActions = new Utils.UserActions();
        utils = new Utils();
    }
    @Test
    @Description("Изменение email пользователя с авторизацией")
    public void updateUserWithAuthorizationEmail() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = methodsUser.generateUniqueEmail();
        Map<String, String> requestBody = createRequestBody(newEmail, password, name);
        logRequest(accessToken, requestBody.toString());
        Response updateResponse = updateUserEmail(accessToken, newEmail, password, name);
        logResponse(updateResponse);
        validateUpdateResponse(updateResponse, newEmail, name);

    }

    @Test
    @Description("Изменение имени пользователя с авторизацией")
    public void updateUserWithAuthorizationName() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newName = methodsUser.generateUniqueName();
        Map<String, String> requestBody = createRequestBody(email, password, name);
        logRequestName(accessToken, requestBody.toString());
        Response updateResponse = updateUserName(accessToken, email, password, newName);
        logResponseName(updateResponse);
        validateUpdateNameResponse(updateResponse, newName, email);

    }

    @Test
    @Description("Изменение пароля пользователя с авторизацией")
    public void updateUserWithAuthorizationPassword() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newPassword = methodsUser.generateUniquePassword();
        Map<String, String> requestBody = createRequestBody(email, newPassword, name);
        logRequestName(accessToken, requestBody.toString());
        Response updateResponse = updateUserPassword(accessToken, newPassword);
        logResponsePassword(updateResponse);
        validateUpdatePasswordResponse(updateResponse, newPassword);

    }

    @Test
    @Description("Изменение всех данных пользователя с авторизацией")
    public void updateUserWithAuthorizationAllFields() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = methodsUser.generateUniqueEmail();
        String newPassword = methodsUser.generateUniquePassword();
        String newName = methodsUser.generateUniqueName();
        Map<String, String> requestBody = createRequestBody(newEmail, newPassword, newName);
        logRequestAllFields(accessToken, requestBody.toString());
        Response updateResponse = updateUserAllFields(accessToken, newEmail, newPassword, newName);
        logResponseAll(updateResponse);
        validateUpdateAllFieldsResponse(updateResponse, newEmail, newName);

    }

    @Test
    @Description("Изменение email пользователя без авторизации")
    public void updateUserWithoutAuthorizationEmail() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String newEmail = methodsUser.generateUniqueEmail();
        Map<String, String> requestBody = createRequestBody(newEmail, password, name);
        logRequestWithoutAuth(requestBody.toString());
        Response updateResponse = updateUserEmailWithoutAuth(newEmail, password, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");

    }

    @Test
    @Description("Изменение имени пользователя без авторизации")
    public void updateUserWithoutAuthorizationName() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String newName = methodsUser.generateUniqueName();
        Map<String, String> requestBody = createRequestBody(email, password, newName);
        logRequestWithoutAuthName(requestBody.toString());
        Response updateResponse = updateUserNameWithoutAuth(email, password, newName);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");

    }

    @Test
    @Description("Изменение пароля пользователя без авторизации")
    public void updateUserWithoutAuthorizationPassword() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String newPassword = methodsUser.generateUniquePassword();
        Map<String, String> requestBody = createRequestBody(email, newPassword, name);
        logRequestWithoutAuthPassword(requestBody.toString());
        Response updateResponse = updateUserPasswordWithoutAuth(email,newPassword, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");

    }

    @Test
    @Description("Изменение всех данных пользователя без авторизации")
    public void updateUserWithoutAuthorizationAllData() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String newEmail = methodsUser.generateUniqueEmail();
        String newPassword = methodsUser.generateUniquePassword();
        String newName = methodsUser.generateUniqueName();
        Map<String, String> requestBody = createRequestBody(newEmail, newPassword, newName);
        logRequestWithoutAuthAll(requestBody.toString());
        Response updateResponse = updateUserAllWithoutAuth(email, newPassword, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");

    }
}

