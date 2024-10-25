package app.controller.impl;

import app.Populator;
import app.SecurityTestUtil;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.dto.GameDTO;
import app.dto.GenreDTO;
import app.dto.PlatformDTO;
import app.entity.*;
import app.mapper.GameMapper;
import app.mapper.GenreMapper;
import app.mapper.PlatformMapper;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

class GameControllerImplTest {

    private static Populator populator;
    private static Javalin app;

    private static PlatformMapper platformMapper;
    private static GenreMapper genreMapper;
    private static GameMapper gameMapper;

    private List<PlatformDTO> platformDTOList;
    private List<GenreDTO> genreDTOList;
    private List<GameDTO> gameDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        int port = 7070;

        populator = new Populator(emf);
        app = AppConfig.startServer(port, emf);

        platformMapper = new PlatformMapper();
        genreMapper = new GenreMapper();
        gameMapper = new GameMapper();

        RestAssured.baseURI = String.format("http://localhost:%d/api", port);
    }

    @BeforeEach
    void setUp() {
        List<Role> roles = populator.createRoles();
        populator.persist(roles);

        List<User> users = populator.createUsers(roles);
        populator.persist(users);

        List<Platform> platforms = populator.createPlatforms();
        populator.persist(platforms);

        List<Genre> genres = populator.createGenres();
        populator.persist(genres);

        List<Game> games = populator.createGames(genres, platforms);
        populator.persist(games);

        platformDTOList = platforms.stream().map(platformMapper::convertToDTO).toList();
        genreDTOList = genres.stream().map(genreMapper::convertToDTO).toList();
        gameDTOList = games.stream().map(gameMapper::convertToDTO).toList();
    }

    @AfterEach
    void tearDown() {
        populator.cleanup(User.class);
        populator.cleanup(Role.class);

        populator.cleanup(Game.class);
        populator.cleanup(Genre.class);
        populator.cleanup(Platform.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    @Test
    void create() {
        GameDTO expected = new GameDTO(
                16L,
                "Game Title 16",
                LocalDate.of(2024, 8, 21),
                "https://example.com/image16.jpg",
                67,
                8,
                "Description for Game 16",
                null,
                null,
                Set.of(platformDTOList.get(7)),
                Set.of(genreDTOList.get(4), genreDTOList.get(5))
        );

        String token = SecurityTestUtil.loginAccount("Admin1", "1234");

        GameDTO actual = given()
                .header("Authorization", "Bearer " + token)
                .body(expected)
                .when()
                .post("/games")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .extract()
                .as(GameDTO.class);

        assertThat(actual, is(expected));
    }

    @Test
    void getById() {
        String token = SecurityTestUtil.loginAccount("User1", "1234");
        GameDTO expected = gameDTOList.get(0);

        GameDTO actual = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/games/{id}", expected.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(GameDTO.class);

        assertThat(actual, is(expected));
    }

    @Test
    void getAll() {
        String token = SecurityTestUtil.loginAccount("User1", "1234");
        List<GameDTO> expected = gameDTOList;

        List<GameDTO> actual = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/games")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .jsonPath()
                .getList("$", GameDTO.class);

        assertThat(actual.size(), is(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void update() {
        GameDTO gameDTO = gameDTOList.get(0);
        GameDTO expected = new GameDTO(
                gameDTO.getId(),
                "Awesome Game Title 11",
                gameDTO.getReleaseDate(),
                gameDTO.getBackgroundImageURL(),
                gameDTO.getMetaCriticScore(),
                25,
                gameDTO.getDescription(),
                gameDTO.getRating(),
                gameDTO.getRatingCount(),
                gameDTO.getPlatformDTOSet(),
                Set.of(genreDTOList.get(2), genreDTOList.get(4))
        );

        String token = SecurityTestUtil.loginAccount("Admin1", "1234");

        GameDTO actual = given()
                .header("Authorization", "Bearer " + token)
                .body(expected)
                .when()
                .put("/games/{id}", expected.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(GameDTO.class);

        assertThat(actual, is(expected));
    }

    @Test
    void delete() {
        String token = SecurityTestUtil.loginAccount("Admin1", "1234");
        GameDTO gameDTO = gameDTOList.get(0);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/games/{id}", gameDTO.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/games/{id}", gameDTO.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.getCode());
    }
}