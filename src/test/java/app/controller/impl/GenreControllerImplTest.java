package app.controller.impl;

import app.PopulatorTestUtil;
import app.SecurityTestUtil;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.dto.GenreDTO;
import app.entity.Genre;
import app.entity.Role;
import app.entity.User;
import app.mapper.GenreMapper;
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

class GenreControllerImplTest {

    private static PopulatorTestUtil populatorTestUtil;
    private static Javalin app;

    private static GenreMapper genreMapper;

    private List<GenreDTO> genreDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        int port = 7070;

        populatorTestUtil = new PopulatorTestUtil(emf);
        app = AppConfig.startServer(port, emf);

        genreMapper = new GenreMapper();

        RestAssured.baseURI = String.format("http://localhost:%d/api", port);
    }

    @BeforeEach
    void setUp() {
        List<Role> roles = populatorTestUtil.createRoles();
        populatorTestUtil.persist(roles);

        List<User> users = populatorTestUtil.createUsers(roles);
        populatorTestUtil.persist(users);

        List<Genre> genres = populatorTestUtil.createGenres();
        populatorTestUtil.persist(genres);

        genreDTOList = genres.stream().map(genreMapper::convertToDTO).toList();
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(User.class);
        populatorTestUtil.cleanup(Role.class);

        populatorTestUtil.cleanup(Genre.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    @Test
    void create() {
        GenreDTO lastGenre = genreDTOList.stream().max(Comparator.comparing(GenreDTO::getId)).orElseThrow();
        GenreDTO expectedGenre = new GenreDTO(
                lastGenre.getId() + 1,
                "Genre name test"
        );

        String token = SecurityTestUtil.loginAccount("Admin1", "1234");

        GenreDTO actualGenre = given()
                .header("Authorization", "Bearer " + token)
                .body(expectedGenre)
                .when()
                .post("/genres")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .extract()
                .as(GenreDTO.class);

        assertThat(actualGenre, is(expectedGenre));
    }

    @Test
    void getById() {
        String token = SecurityTestUtil.loginAccount("User1", "1234");
        GenreDTO expectedGenreDTO = genreDTOList.get(0);

        GenreDTO actualGenreDTO = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/genres/{id}", expectedGenreDTO.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(GenreDTO.class);

        assertThat(actualGenreDTO, is(expectedGenreDTO));
    }

    @Test
    void getAll() {
        String token = SecurityTestUtil.loginAccount("User1", "1234");
        List<GenreDTO> expectedGenreDTOList = genreDTOList;

        List<GenreDTO> actualGenreDTOList = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/genres")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .jsonPath()
                .getList("$", GenreDTO.class);

        assertThat(actualGenreDTOList.size(), is(expectedGenreDTOList.size()));
        assertThat(actualGenreDTOList, containsInAnyOrder(expectedGenreDTOList.toArray()));
    }

    @Test
    void update() {
        GenreDTO expectedGenreDTO = genreDTOList.get(0);
        expectedGenreDTO.setName("New genre name");

        String token = SecurityTestUtil.loginAccount("Admin1", "1234");

        GenreDTO actualGenreDTO = given()
                .header("Authorization", "Bearer " + token)
                .body(expectedGenreDTO)
                .when()
                .put("/genres/{id}", expectedGenreDTO.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(GenreDTO.class);

        assertThat(actualGenreDTO, is(expectedGenreDTO));
    }

    @Test
    void delete() {
        String token = SecurityTestUtil.loginAccount("Admin1", "1234");
        GenreDTO expectedGenreDTO = genreDTOList.get(0);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/genres/{id}", expectedGenreDTO.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/genres/{id}", expectedGenreDTO.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.getCode());
    }
}