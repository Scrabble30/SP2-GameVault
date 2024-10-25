package app.dao.impl;

import app.PopulatorTestUtil;
import app.config.HibernateConfig;
import app.entity.Game;
import app.entity.Genre;
import app.entity.Platform;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class GameDAOImplTest {

    private static PopulatorTestUtil populatorTestUtil;
    private static GameDAOImpl gameDAO;

    private List<Genre> genres;
    private List<Platform> platforms;
    private List<Game> games;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populatorTestUtil = new PopulatorTestUtil(emf);
        gameDAO = GameDAOImpl.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        genres = populatorTestUtil.createGenres();
        populatorTestUtil.persist(genres);

        platforms = populatorTestUtil.createPlatforms();
        populatorTestUtil.persist(platforms);

        games = populatorTestUtil.createGames(genres, platforms);
        populatorTestUtil.persist(games);
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(Game.class);
        populatorTestUtil.cleanup(Genre.class);
        populatorTestUtil.cleanup(Platform.class);
    }

    @Test
    void create() {
        Game lastGame = games.stream().max(Comparator.comparing(Game::getId)).orElseThrow();

        Game expectedGame = Game.builder()
                .id(lastGame.getId() + 1)
                .title("This is a new game")
                .releaseDate(LocalDate.of(2024, 10, 22))
                .backgroundImageURL("https://example.com/imageOfNewGame.jpg")
                .metaCriticScore(70)
                .playtime(30)
                .description("Description for the new game")
                .platformSet(Set.of(platforms.get(3)))
                .genreSet(Set.of(genres.get(5)))
                .build();

        Game actualGame = gameDAO.create(expectedGame);

        assertThat(actualGame, is(expectedGame));
    }

    @Test
    void getById() {
        Game expectedGame = games.get(0);

        Game actualGame = gameDAO.getById(expectedGame.getId());

        assertThat(actualGame.getId(), is(expectedGame.getId()));
        assertThat(actualGame, is(expectedGame));
    }

    @Test
    void getAll() {
        Set<Game> expectedGames = new HashSet<>(games);

        Set<Game> actualGames = gameDAO.getAll();

        assertThat(actualGames.size(), is(expectedGames.size()));
        assertThat(actualGames, containsInAnyOrder(expectedGames.toArray()));
    }

    @Test
    void update() {
        Game expectedGame = games.get(1);
        expectedGame.setTitle("New Game Title");
        expectedGame.setGenreSet(Set.of(genres.get(0)));
        expectedGame.setPlatformSet(Set.of(platforms.get(0)));

        Game actualGame = gameDAO.update(expectedGame.getId(), expectedGame);

        assertThat(actualGame, is(expectedGame));
    }

    @Test
    void delete() {
        Game gameToDelete = games.get(2);

        gameDAO.delete(gameToDelete.getId());

        assertThrowsExactly(EntityNotFoundException.class, () -> gameDAO.getById(gameToDelete.getId()));
    }
}