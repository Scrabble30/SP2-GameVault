package app.controller.impl;

import app.PopulatorTestUtil;
import app.SecurityTestUtil;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.dto.GameDTO;
import app.dto.GenreDTO;
import app.dto.PlatformDTO;
import app.entity.*;
import app.filter.GameFilter;
import app.mapper.impl.GameMapperImpl;
import app.mapper.impl.GenreMapperImpl;
import app.mapper.impl.PlatformMapperImpl;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

class GameControllerImplTest {

    private static PopulatorTestUtil populatorTestUtil;
    private static Javalin app;

    private static PlatformMapperImpl platformMapper;
    private static GenreMapperImpl genreMapper;
    private static GameMapperImpl gameMapper;

    private List<PlatformDTO> platformDTOList;
    private List<GenreDTO> genreDTOList;
    private List<GameDTO> gameDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        int port = 7070;

        populatorTestUtil = new PopulatorTestUtil(emf);
        app = AppConfig.startServer(port, emf);

        platformMapper = PlatformMapperImpl.getInstance();
        genreMapper = GenreMapperImpl.getInstance();
        gameMapper = GameMapperImpl.getInstance();

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

        List<Genre> genres = populatorTestUtil.createGenres();
        populatorTestUtil.persist(genres);

        List<Game> games = populatorTestUtil.createGames(genres, platforms);
        populatorTestUtil.persist(games);

        platformDTOList = platforms.stream().map(platformMapper::convertToDTO).toList();
        genreDTOList = genres.stream().map(genreMapper::convertToDTO).toList();
        gameDTOList = games.stream().map(gameMapper::convertToDTO).toList();
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(User.class);
        populatorTestUtil.cleanup(Role.class);

        populatorTestUtil.cleanup(Game.class);
        populatorTestUtil.cleanup(Genre.class);
        populatorTestUtil.cleanup(Platform.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    List<GameDTO> filterGames(GameFilter gameFilter) {
        return gameDTOList.stream().filter(game -> {
            boolean include = true;

            if (gameFilter.getTitle() != null)
                include = game.getTitle().toLowerCase().contains(gameFilter.getTitle().toLowerCase());
            if (gameFilter.getReleaseDateGTE() != null)
                include = include && !game.getReleaseDate().isBefore(gameFilter.getReleaseDateGTE());
            if (gameFilter.getReleaseDateLTE() != null)
                include = include && !game.getReleaseDate().isAfter(gameFilter.getReleaseDateLTE());

            return include;
        }).toList();
    }

    private long getGamesPageCount(Integer pageSize, Long gamesCount) {
        if (pageSize == 0 || gamesCount == 0) {
            return 0;
        }

        return (long) Math.ceil((double) gamesCount / pageSize);
    }

    String convertFilterToQueryString(GameFilter gameFilter) {
        List<String> params = new LinkedList<>();

        if (gameFilter.getPageNumber() != null)
            params.add(String.format("page_number=%s", gameFilter.getPageNumber()));
        if (gameFilter.getPageSize() != null)
            params.add(String.format("page_size=%s", gameFilter.getPageSize()));
        if (gameFilter.getTitle() != null)
            params.add(String.format("name=%s", gameFilter.getTitle()));
        if (gameFilter.getReleaseDateGTE() != null)
            params.add(String.format("released.gte=%s", gameFilter.getReleaseDateGTE()));
        if (gameFilter.getReleaseDateLTE() != null)
            params.add(String.format("released.lte=%s", gameFilter.getReleaseDateLTE()));

        return String.join("&", params);
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
        GameDTO expected = gameDTOList.get(0);

        GameDTO actual = given()
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
        List<GameDTO> expected = gameDTOList;

        List<GameDTO> actual = given()
                .when()
                .get("/games")
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .jsonPath()
                .getList("results", GameDTO.class);

        assertThat(actual.size(), is(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void getAllFiltered() {
        GameFilter gameFilter = new GameFilter(
                0,
                2,
                "Game Title",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 1)
        );

        List<GameDTO> filteredGames = filterGames(gameFilter);

        int startIndex = Math.min(filteredGames.size(), gameFilter.getPageNumber() * gameFilter.getPageSize());
        int endIndex = Math.min(filteredGames.size(), startIndex + gameFilter.getPageSize());

        List<GameDTO> expected = filteredGames.subList(startIndex, endIndex);

        Integer expectedPageNumber = gameFilter.getPageNumber();
        Integer expectedTotalResults = filteredGames.size();
        Integer expectedTotalPages = (int) getGamesPageCount(gameFilter.getPageSize(), (long) expectedTotalResults);

        List<GameDTO> actual = given()
                .when()
                .get(String.format("/games?%s", convertFilterToQueryString(gameFilter)))
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .body("page_number", is(expectedPageNumber))
                .body("total_pages", equalTo(expectedTotalPages))
                .body("total_results", is(expectedTotalResults))
                .extract()
                .jsonPath()
                .getList("results", GameDTO.class);

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