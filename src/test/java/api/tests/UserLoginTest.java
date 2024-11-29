package api.tests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.Test;


@Epic("User  Management")
@Feature("User  Login")
public class UserLoginTest extends BeforeAndAfter {
    private MethodsOrders methodsOrders;
    private MethodsUser  methodsUser ;
    private Utils.UserActions userActions;
    private Utils utils;
    public UserLoginTest() {
        methodsOrders = new MethodsOrders();
        methodsUser  = new MethodsUser ();
        utils = new Utils();
    }

    @Test
    @Description("Логин под существующим пользователем с проверкой успешного ответа")
    public void loginWithExistingUserTest() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response response = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(response, email, name);
        Response loginResponse = methodsUser.loginWithUser(email, password);
        String accessToken = utils.verifyLoginSuccess(loginResponse);
    }

    @Test
    @Description("Логин с верным именем, но неверным email и паролем")
    public void loginWithInvalidCredentials() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response createUserResponse = methodsUser.createUniqueUser(email, password, name);
        userActions.verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String invalidEmail = "invalid" + email;
        String invalidPassword = "wrongPassword";
        Response loginResponse = methodsUser.loginWithUser(invalidEmail, invalidPassword);
        utils.verifyLoginWithInvalidCredentials(loginResponse);
    }

}