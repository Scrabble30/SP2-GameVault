package app.daos.impl;

import app.Populator;
import app.config.HibernateConfig;
import app.entities.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ReviewDAOImplTest {

    private static Populator populator;
    private static GameDAOImpl gameDAO;
    private static ReviewDAOImpl reviewDAO;

    private List<User> users;
    private List<Game> games;
    private List<Review> reviews;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populator = new Populator(emf);
        gameDAO = GameDAOImpl.getInstance(emf);
        reviewDAO = ReviewDAOImpl.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        List<Role> roles = populator.createRoles();
        populator.persist(roles);

        users = populator.createUsers(roles);
        populator.persist(users);

        List<Genre> genres = populator.createGenres();
        populator.persist(genres);

        List<Platform> platforms = populator.createPlatforms();
        populator.persist(platforms);

        games = populator.createGames(genres, platforms);
        reviews = populator.createReviews(users, games);

        populator.persist(games);
        populator.persist(reviews);
    }

    @AfterEach
    void tearDown() {
        populator.cleanup(Review.class);
        populator.cleanup(Game.class);
        populator.cleanup(Platform.class);
        populator.cleanup(Genre.class);
        populator.cleanup(User.class);
        populator.cleanup(Role.class);
    }

    @Test
    void create() {
        User user = users.get(1);
        Game game = games.get(1);

        Review expected = Review.builder()
                .username(user.getUsername())
                .gameId(game.getId())
                .rating(7.5)
                .review("This movie was pretty decent but could use a little more work")
                .build();

        Review actual = reviewDAO.create(expected);

        assertThat(actual.getId(), notNullValue());
        assertThat(actual, is(expected));

        List<Review> gameReviews = reviews.stream().filter(review -> Objects.equals(review.getGameId(), game.getId())).collect(Collectors.toList());
        gameReviews.add(actual);

        Double expectedRating = gameReviews.stream().mapToDouble(Review::getRating).average().orElse(0);
        Long expectedCount = (long) gameReviews.size();

        Game actualGame = gameDAO.getById(game.getId());

        assertThat(actualGame.getRating(), is(expectedRating));
        assertThat(actualGame.getRatingCount(), is(expectedCount));
    }

    @Test
    void getById() {
        Review expected = reviews.get(0);
        Review actual = reviewDAO.getById(expected.getId());

        assertThat(actual, is(expected));
    }

    @Test
    void getByGameId() {
        Game game = games.get(0);

        Set<Review> expected = reviews.stream().filter(review -> Objects.equals(review.getGameId(), game.getId())).collect(Collectors.toSet());
        Set<Review> actual = reviewDAO.getByGameId(game.getId());

        assertThat(actual.size(), is(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void getAll() {
        Set<Review> expected = new HashSet<>(reviews);
        Set<Review> actual = reviewDAO.getAll();

        assertThat(actual.size(), is(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void update() {
        Review expected = reviews.get(0);
        expected.setRating(8.8);

        Review actual = reviewDAO.update(expected.getId(), expected);

        assertThat(actual, is(expected));

        List<Review> gameReviews = reviews.stream().filter(review -> Objects.equals(review.getGameId(), expected.getGameId())).collect(Collectors.toList());

        Double expectedRating = gameReviews.stream().mapToDouble(Review::getRating).average().orElse(0);
        Long expectedCount = (long) gameReviews.size();

        Game actualGame = gameDAO.getById(expected.getGameId());

        assertThat(actualGame.getRating(), is(expectedRating));
        assertThat(actualGame.getRatingCount(), is(expectedCount));
    }

    @Test
    void delete() {
        Review review = reviews.get(0);

        reviewDAO.delete(review.getId());
        assertThrowsExactly(EntityNotFoundException.class, () -> reviewDAO.getById(review.getId()));
    }
}