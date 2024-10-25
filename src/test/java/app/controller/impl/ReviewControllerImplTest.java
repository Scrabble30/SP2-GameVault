package app.controller.impl;

import app.PopulatorTestUtil;
import app.SecurityTestUtil;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.dto.GameDTO;
import app.dto.ReviewDTO;
import app.entity.*;
import app.mapper.GameMapper;
import app.mapper.ReviewMapper;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ReviewControllerImplTest {

    private static Javalin app;
    private static PopulatorTestUtil populatorTestUtil;
    private static GameMapper gameMapper;
    private static ReviewMapper reviewMapper;

    private List<GameDTO> gameDTOList;
    private List<ReviewDTO> reviewDTOList;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
        int port = 7070;

        populatorTestUtil = new PopulatorTestUtil(emf);
        app = AppConfig.startServer(port, emf);

        gameMapper = new GameMapper();
        reviewMapper = new ReviewMapper();

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

        List<Platform> platforms = populatorTestUtil.createPlatforms();
        populatorTestUtil.persist(platforms);

        List<Game> games = populatorTestUtil.createGames(genres, platforms);
        populatorTestUtil.persist(games);

        List<Review> reviews = populatorTestUtil.createReviews(users, games);
        populatorTestUtil.persist(reviews);

        gameDTOList = games.stream().map(gameMapper::convertToDTO).toList();
        reviewDTOList = reviews.stream().map(reviewMapper::convertToDTO).toList();
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(User.class);
        populatorTestUtil.cleanup(Role.class);

        populatorTestUtil.cleanup(Review.class);
        populatorTestUtil.cleanup(Game.class);
        populatorTestUtil.cleanup(Genre.class);
        populatorTestUtil.cleanup(Platform.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer(app);
    }

    @Test
    void getById() {
        ReviewDTO expectedReviewDTO = reviewDTOList.get(2);

        ReviewDTO actualReviewDTO = given()
                .when()
                .get("reviews/{id}", expectedReviewDTO.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(ReviewDTO.class);

        assertThat(actualReviewDTO, is(expectedReviewDTO));
    }

    @Test
    void getAll() {
        GameDTO gameDTO = gameDTOList.get(1);
        List<ReviewDTO> expectedReviews = reviewDTOList.stream().filter(review -> review.getGameId().equals(gameDTO.getId())).toList();

        List<ReviewDTO> actualReviews = given()
                .when()
                .get("/games/{id}/reviews", gameDTO.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .jsonPath()
                .getList("$", ReviewDTO.class);

        assertThat(actualReviews.size(), is(expectedReviews.size()));
        assertThat(actualReviews, containsInAnyOrder(expectedReviews.toArray()));
    }

    @Test
    void create() {
        String username = "User2";
        String token = SecurityTestUtil.loginAccount(username, "1234");

        GameDTO gameDTO = gameDTOList.get(4);

        ReviewDTO expectedReviewDTO = new ReviewDTO(
                null,
                username,
                gameDTO.getId(),
                7.5,
                "Really Good!"
        );

        ReviewDTO actualReviewDTO = given()
                .header("Authorization", "Bearer " + token)
                .body(expectedReviewDTO)
                .when()
                .post("/reviews")
                .then()
                .statusCode(HttpStatus.CREATED.getCode())
                .extract()
                .as(ReviewDTO.class);

        expectedReviewDTO.setId(actualReviewDTO.getId());

        assertThat(actualReviewDTO.getId(), notNullValue());
        assertThat(actualReviewDTO, is(expectedReviewDTO));
    }

    @Test
    void update() {
        String token = SecurityTestUtil.loginAccount("User2", "1234");

        ReviewDTO expectedReviewDTO = reviewDTOList.get(2);
        expectedReviewDTO.setReview("The ending was satisfying");
        expectedReviewDTO.setRating(5.5);

        ReviewDTO actualReviewDTO = given()
                .header("Authorization", "Bearer " + token)
                .body(expectedReviewDTO)
                .when()
                .put("/reviews/{id}", expectedReviewDTO.getId())
                .then()
                .statusCode(HttpStatus.OK.getCode())
                .extract()
                .as(ReviewDTO.class);

        assertThat(actualReviewDTO, is(expectedReviewDTO));
    }

    @Test
    void delete() {
        String token = SecurityTestUtil.loginAccount("User1", "1234");

        ReviewDTO reviewDTO = reviewDTOList.get(0);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/reviews/{id}", reviewDTO.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.getCode());

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/reviews/{id}", reviewDTO.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.getCode());
    }
}