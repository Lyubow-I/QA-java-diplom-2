package api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Test;


@Epic("Управление пользователями")
@Feature("Обновление пользователя")
public class UserUpdateTest extends AllMethods {

    private AllMethods methodsUserLogin = new AllMethods();
    @Test
    @Description("Изменение email пользователя с авторизацией")
    public void updateUserWithAuthorizationEmail() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = generateUniqueEmail();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
        logRequest(accessToken, requestBody);
        Response updateResponse = updateUserEmail(accessToken, newEmail, password, name);
        logResponse(updateResponse);
        validateUpdateResponse(updateResponse, newEmail, name);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение имени пользователя с авторизацией")
    public void updateUserWithAuthorizationName() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newName = generateUniqueName();
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
        logRequestName(accessToken, requestBody);
        Response updateResponse = updateUserName(accessToken, email, password, newName);
        logResponseName(updateResponse);
        validateUpdateNameResponse(updateResponse, newName, email);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение пароля пользователя с авторизацией")
    public void updateUserWithAuthorizationPassword() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newPassword = generateUniquePassword();
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" +newPassword + "\"}";
        logRequestPassword(accessToken, requestBody);
        Response updateResponse = updateUserPassword(accessToken, newPassword);
        logResponsePassword(updateResponse);
        validateUpdatePasswordResponse(updateResponse, newPassword);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение всех данных пользователя с авторизацией")
    public void updateUserWithAuthorizationAllFields() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = generateUniqueEmail();
        String newPassword = generateUniquePassword();
        String newName = generateUniqueName();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
        logRequestAllFields(accessToken, requestBody);
        Response updateResponse = updateUserAllFields(accessToken, newEmail, newPassword, newName);
        logResponseAll(updateResponse);
        validateUpdateAllFieldsResponse(updateResponse, newEmail, newName);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение email пользователя без авторизации")
    public void updateUserWithoutAuthorizationEmail() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newEmail = generateUniqueEmail();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + name + "\", \"password\":\"" + password + "\"}";
        logRequestWithoutAuth(requestBody);
        Response updateResponse = updateUserEmailWithoutAuth(newEmail, password, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение имени пользователя без авторизации")
    public void updateUserWithoutAuthorizationName() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newName = generateUniqueName();
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
        logRequestWithoutAuthName(requestBody);
        Response updateResponse = updateUserNameWithoutAuth(email, password, newName);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение пароля пользователя без авторизации")
    public void updateUserWithoutAuthorizationPassword() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newPassword = generateUniquePassword();
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" + newPassword + "\"}";
        logRequestWithoutAuthPassword(requestBody);
        Response updateResponse = updateUserPasswordWithoutAuth(email, newPassword, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение всех данных пользователя без авторизации")
    public void updateUserWithoutAuthorizationAllData() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newEmail = generateUniqueEmail();
        String newPassword = generateUniquePassword();
        String newName = generateUniqueName();
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
        logRequestWithoutAuthAll(requestBody);
        Response updateResponse = updateUserAllWithoutAuth(email, newPassword, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        deleteUserByToken(accessToken);
    }
}

