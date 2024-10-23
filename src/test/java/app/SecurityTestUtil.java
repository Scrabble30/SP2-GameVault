package app;

import app.dto.UserDTO;

import static io.restassured.RestAssured.given;

public class SecurityTestUtil {

    public static String loginAccount(String username, String password) {
        UserDTO userDTO = new UserDTO(username, password);

        return given()
                .body(userDTO)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
