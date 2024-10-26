package app.controller.impl;

import app.PopulatorTestUtil;
import app.SecurityTestUtil;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.dto.PlatformDTO;
import app.entity.Platform;
import app.entity.Role;
import app.entity.User;
import app.mapper.impl.PlatformMapperImpl;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

class PlatformControllerImplTest {

    private static PopulatorTestUtil populatorTestUtil;
    private static Javalin app;

    private static PlatformMapperImpl platformMapper;

    private List<PlatformDTO> platformDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        int port = 7070;

        populatorTestUtil = new PopulatorTestUtil(emf);
        app = AppConfig.startServer(port, emf);

        platformMapper = PlatformMapperImpl.getInstance();

        RestAssured.baseURI = String.format("http://localhost:%d/api", port);
    }

    @BeforeEach
    void setUp() {
        List<Role> roles = populatorTestUtil.createRoles();
        populatorTestUtil.persist(roles);

        List<User> users = populatorTestUtil.createUsers(roles);
        populatorTestUtil.persist(users);

        List<Platform> platforms = populatorTestUtil.createPlatforms();
        populatorTestUtil.persist(platforms);

        platformDTOList = platforms.stream().map(platformMapper::convertToDTO).toList();
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(User.class);
        populatorTestUtil.cleanup(Role.class);

        populatorTestUtil.cleanup(Platform.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    @Test
    void create() {
        PlatformDTO lastPlatformDTO = platformDTOList.stream().max(Comparator.comparing(PlatformDTO::getId)).orElseThrow();
        PlatformDTO expected = new PlatformDTO(
                lastPlatformDTO.getId() + 1,
                "New Platform"
        );

        String token = SecurityTestUtil.loginAccount("Admin1", "1234");

        PlatformDTO actual = given()
                .header("Authorization", "Bearer " + token)
                .body(expected)
                .when()
                .post("/platforms")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .extract()
                .as(PlatformDTO.class);

        assertThat(actual, is(expected));
    }

    @Test
    void getById() {
        PlatformDTO expected = platformDTOList.get(0);

        PlatformDTO actual = given()
                .when()
                .get("/platforms/{id}", expected.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(PlatformDTO.class);

        assertThat(actual, is(expected));
    }

    @Test
    void getAll() {
        List<PlatformDTO> expected = platformDTOList;

        List<PlatformDTO> actual = given()
                .when()
                .get("/platforms")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .jsonPath()
                .getList("$", PlatformDTO.class);

        assertThat(actual.size(), is(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void update() {
        PlatformDTO platformDTO = platformDTOList.get(0);
        PlatformDTO expected = new PlatformDTO(
                platformDTO.getId(),
                "New Platform Name"
        );

        String token = SecurityTestUtil.loginAccount("Admin1", "1234");

        PlatformDTO actual = given()
                .header("Authorization", "Bearer " + token)
                .body(expected)
                .when()
                .put("/platforms/{id}", expected.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(PlatformDTO.class);

        assertThat(actual, is(expected));
    }

    @Test
    void delete() {
        String token = SecurityTestUtil.loginAccount("Admin1", "1234");
        PlatformDTO platformDTO = platformDTOList.get(0);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/platforms/{id}", platformDTO.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/platforms/{id}", platformDTO.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.getCode());
    }
}