package api.tests;

import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;

public class BeforeAndAfter {
    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    protected static final String REGISTER_URL = BASE_URL + "/api/auth/register";
    protected static final String LOGIN_URL = BASE_URL + "/api/auth/login";
    protected static final String DELETE_USER_URL = BASE_URL + "/api/auth/user";
    protected static final String ORDERS_URL = BASE_URL + "/api/orders";
    protected static final String INGREDIENTS_URL = BASE_URL + "/api/ingredients";
    protected String accessToken;
    private MethodsUser  methodsUser;


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            methodsUser.deleteUserByToken(accessToken);
        }
    }
}
