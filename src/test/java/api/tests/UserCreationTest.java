package api.tests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.Test;

@Epic("User  Management")
@Feature("User  Creation and Deletion")
public class UserCreationTest extends BeforeAndAfter {
    private MethodsOrders methodsOrders;
    private MethodsUser  methodsUser ;
    private Utils.UserActions userActions;
    private Utils utils;
    public UserCreationTest() {
        methodsOrders = new MethodsOrders();
        methodsUser  = new MethodsUser ();
        userActions = new Utils.UserActions();
        utils = new Utils();
    }
    @Test
    @Description("Создание уникального пользователя и удаление после проверки")
    public void creationUniqueUser() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response response = methodsUser.createUniqueUser(email, password, name);
        String accessToken = userActions.verifyUserCreation(response, email, name);
    }

    @Test
    @Description("Создание зарегистрированного пользователя и проверка на ошибку повторной регистрации")
    public void createExistingUser() {
        String email = methodsUser.generateUniqueEmail();
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response firstResponse = methodsUser.createUniqueUser(email, password, name);
        utils.verifyUserCreationSuccess(firstResponse);
        accessToken = firstResponse.jsonPath().getString("accessToken");
        Response secondResponse = methodsUser.createUniqueUser(email, password, name);
        utils.verifyDuplicateUserError(secondResponse);
    }

    @Test
    @Description("Создание пользователя без пароля и проверка на ошибку")
    public void createUserWithoutRequiredFieldPassword() {
        String email = methodsUser.generateUniqueEmail();
        String name = methodsUser.generateUniqueName();
        Response response = methodsUser.createUniqueUserWithoutPassword(email, name);
        utils.verifyUserCreationFailurePassword(response);
    }

    @Test
    @Description("Создание пользователя без email и проверка на ошибку")
    public void createUserWithoutRequiredFieldEmail() {
        String password = methodsUser.generateUniquePassword();
        String name = methodsUser.generateUniqueName();
        Response response = methodsUser.createUniqueUserWithoutEmail(password, name);
        utils.verifyUserCreationFailureEmail(response, "Email, password and name are required fields");
    }

    @Test
    @Description("Создание пользователя без имени и проверка на ошибку")
    public void createUserWithoutRequiredFieldName() {
        String password = methodsUser.generateUniquePassword();
        String email = methodsUser.generateUniqueEmail();
        Response response = methodsUser.createUniqueUserWithoutName(password, email);
        utils.verifyUserCreationFailureName(response, "Email, password and name are required fields");
    }
}


