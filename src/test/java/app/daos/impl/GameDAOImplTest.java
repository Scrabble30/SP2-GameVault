package app.daos.impl;

import app.Populator;
import app.config.HibernateConfig;
import app.entities.Game;
import app.entities.Genre;
import app.entities.Platform;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GameDAOImplTest {

    private static Populator populator;
    private static GameDAOImpl gameDAO;
    private List<Genre> genres;
    private List<Platform> platforms;
    private List<Game> games;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        gameDAO = GameDAOImpl.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        genres = populator.createGenres();
        populator.persist(genres);

        platforms = populator.createPlatforms();
        populator.persist(platforms);

        games = populator.createGames(genres, platforms);
        populator.persist(games);
    }

    @AfterEach
    void tearDown() {
        populator.cleanup(Game.class);
        populator.cleanup(Genre.class);
        populator.cleanup(Platform.class);
    }

    @Test
    void create() {
        List<Game> sortedGames = games.stream()
                .sorted(Comparator.comparing(Game::getId))
                .toList();

        Long lastId = sortedGames.get(sortedGames.size() - 1).getId();
        Game expectedGame = Game.builder()
                .id(lastId + 1)
                .title("This is a new game")
                .releaseDate(LocalDate.of(2024, 10, 22))
                .backgroundImageURL("https://example.com/imageOfNewGame.jpg")
                .metaCriticScore(70.5)
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
    }

    @Test
    void getAll() {
        Set<Game> expectedGames = new HashSet<>(games);

        Set<Game> actualGames = gameDAO.getAll();

        assertNotNull(actualGames);
        assertThat(actualGames.size(), is(expectedGames.size()));
        assertThat(actualGames, is(expectedGames));
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